#!/usr/bin/env python
# -*- coding: utf8 -*-

'''
pre:

1. grep -h Java_org_cocos2dx_lib_ -R ../../../../../sdks/cocos2d-x-2.2.2/ >> cocosloader/cocos_binding
2. delete wrong function definitions
3. cat cocosloader/cocos_binding | sed -e 's/^ *//' -e 's/ *$//' | sort | uniq
4. echo "JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Config_initPath(JNIEnv* env, jobject thiz, jboolean useAssets, jstring resPath)" >> cocosloader/binding.c
5. cat cocosloader/cocos_binding | ../../tools/binding_gen.py > cocosloader/binding.c
'''

import sys, re


defs = []
dlsyms = []
func_impls = []

c_file_templ = '''
// auto generated code, do not modify.

#include <stdio.h>
#include <jni.h>
#include <dlfcn.h>
#include <android/log.h>
#include "loader.h"

%(defs)s
static int (*JNI_OnLoad_0)(JavaVM* vm, void* reserved);

int init_binding(JavaVM* vm, void* reserved, void* handler) {
%(dlsyms)s

    JNI_OnLoad_0 = dlsym(handler, "JNI_OnLoad");
    if((JNI_OnLoad_0) == NULL) {
        return -2;
    }

    JNI_OnLoad_0(vm, reserved);
    return 0;
}


%(func_impls)s


'''
def_tmpl = 'static %s (*%s_0)%s;'
dlsym_templ = '''
    %s_0 = dlsym(handler, "%s");
    if((%s_0) == NULL) {
        return -2;
    }
'''
func_templ = '''
JNIEXPORT %(ret_type)s JNICALL %(func_name)s%(args_list)s {
    return (*%(func_name)s_0)(%(args)s);
}
'''


def gen_def(ret_type, func_name, args_list):
    defs.append( def_tmpl % (ret_type, func_name, args_list))

def gen_export(func_name):
    dlsyms.append(dlsym_templ % (func_name, func_name, func_name))

def gen_func(ret_type, func_name, args_list):
    args = args_list.strip('() ').split(',')
    args = filter(lambda x: x, args)
    arg_names = [];
    for arg in args:
        m = re.match('^(.*)\s+([0-9a-zA-Z_]*$)', arg.strip())
        matched = m.groups()
        if not m:
            raise Exception('parse arg failed: ' + arg)
        arg_type = matched[0]
        arg_name = matched[1]
        arg_names.append(arg_name)
    func_impls.append(func_templ % {
        'ret_type': ret_type,
        'func_name': func_name,
        'args_list': args_list,
        'args': ','.join(arg_names),
    })


def handle(line):
    line = line.strip()
    m = re.match(r'^JNIEXPORT\s+(.*)\s+JNICALL\s+([0-9a-zA-Z_]*)([^{]*)\{?\s*', line)
    if not m:
        raise Exception('match line failed: ' + line)
    matched = m.groups()
    ret_type = matched[0]
    func_name = matched[1]
    args_list = matched[2]
    gen_def(ret_type, func_name, args_list)
    gen_export(func_name)
    gen_func(ret_type, func_name, args_list)



def print_code():
    print c_file_templ % {
        'defs': '\n'.join(defs),
        'dlsyms': '\n'.join(dlsyms),
        'func_impls': '\n\n\n'.join(func_impls),
    }


if __name__ == '__main__':
    for line in sys.stdin:
        if not line:
            continue
        handle(line)
    print_code()