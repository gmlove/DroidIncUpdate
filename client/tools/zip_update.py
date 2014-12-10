#!/usr/bin/env python
# -*- coding: utf8 -*-
import sys, os
import json
import math
import string
import re
import datetime
import hashlib
import shutil
import re
import subprocess
import ConfigParser as cparser
from os import path
import env
from env import BASE_DIR, LIBS_DIR, RES_DIR,INC_UPDATE_FILE_PATH, \
scanFileList, genFileMD5, write2File, Log, BACKUP_DIR, \
BACKUP_LIBS_DIR, BACKUP_RES_DIR, DIFF_DIR, DIFF_UPDATE_DIR, PATCH_FILE_MIN_SIZE, \
PATCH_FILE_MAX_SIZE_RATIO, DIFF_RESULT_FILE, FILE_VERSIONS_FILE_PATH, getPackageName
from update_limit import limit_templ, supported_limit_keys, parse_limit, update_limit, find_path

'''
1. backup all resource, and generate version
2. generate update.zip for every older version
    if file too large, generate a patch file
'''

proj_path = '.'


def isbackupdir(dirname):
    return bool(re.match('^[0-9]{14}-[0-91-z]{32}$', dirname))


def backup(version):
    Log.info('backup resource files start.')
    dirname = datetime.datetime.now().strftime('%Y%m%d%H%M%S') + '-' + version;
    target_dir = os.path.join(proj_path, env.INCUPDATE_DATA_DIR, dirname)
    shutil.copytree(path.join(proj_path, LIBS_DIR),
        path.join(target_dir, BACKUP_DIR, BACKUP_LIBS_DIR))
    Log.debug('copytree %s %s', path.join(proj_path, LIBS_DIR),
        path.join(target_dir, BACKUP_DIR, BACKUP_LIBS_DIR))
    shutil.copytree(path.join(proj_path, RES_DIR),
        path.join(target_dir, BACKUP_DIR, BACKUP_RES_DIR))
    Log.debug('copytree %s %s', path.join(proj_path, RES_DIR),
        path.join(target_dir, BACKUP_DIR, BACKUP_RES_DIR))
    shutil.copy2(path.join(proj_path, INC_UPDATE_FILE_PATH),
        path.join(target_dir, BACKUP_DIR))
    Log.debug('copyfile %s %s', path.join(proj_path, INC_UPDATE_FILE_PATH),
        path.join(target_dir, BACKUP_DIR))
    shutil.copy2(path.join(proj_path, FILE_VERSIONS_FILE_PATH),
        path.join(target_dir, BACKUP_DIR))
    Log.debug('copyfile %s %s', path.join(proj_path, FILE_VERSIONS_FILE_PATH),
        path.join(target_dir, BACKUP_DIR))
    Log.info('backup resource files end: %s', dirname)
    return dirname


def patchFile(old_file, new_file, dst_file):
    Log.debug('patch file start: old_file=%s, new_file=%s, dst_file=%s', old_file, new_file, dst_file)
    res = subprocess.call(['bsdiff', old_file, new_file, dst_file])
    Log.debug('patch file end: old_file=%s, new_file=%s, dst_file=%s', old_file, new_file, dst_file)
    if res != 0:
        raise 'bsdiff failed'



def genFileUpdate(new_file, old_file, dst_dir):
    Log.debug('genFileUpdate: new_file=%s, old_file=%s, dst_dir=%s', new_file, old_file, dst_dir)
    assert os.path.exists(new_file)
    #import pdb;pdb.set_trace()
    if(os.path.isdir(new_file)):
        return None
    if not os.path.exists(dst_dir):
        os.makedirs(dst_dir)

    if not os.path.exists(old_file):
        md5 = genFileMD5(new_file)
        shutil.copy2(new_file, os.path.join(dst_dir, md5))
        return 'replace', '', '', md5, md5

    old_md5 = genFileMD5(old_file)
    new_md5 = genFileMD5(new_file)
    if old_md5 == new_md5:
        return None;

    old_size = os.path.getsize(old_file)
    new_size = os.path.getsize(new_file)
    use_patch = True
    patch_file = os.path.join(dst_dir, old_md5 + '-' + new_md5)
    if new_size >= PATCH_FILE_MIN_SIZE:
        patchFile(old_file, new_file, patch_file)
        patch_size = os.path.getsize(patch_file)
        if patch_size > new_size * PATCH_FILE_MAX_SIZE_RATIO:
            use_patch = False
            os.remove(patch_file)
            assert not os.path.exists(patch_file)
    else:
        use_patch = False

    if not use_patch:
        shutil.copy2(new_file, os.path.join(dst_dir, new_md5))
        return 'replace', old_md5, '', new_md5, new_md5

    patch_md5 = genFileMD5(patch_file)
    return 'patch', old_md5, patch_md5, new_md5, os.path.basename(patch_file)


def genUpdate(new_version_dir, old_version_dir, version):
    Log.debug('genUpdate: new_version_dir=%s, old_version_dir=%s', new_version_dir, old_version_dir)
    new_dir = os.path.join(proj_path, env.INCUPDATE_DATA_DIR, new_version_dir)
    old_dir = os.path.join(proj_path, env.INCUPDATE_DATA_DIR, old_version_dir)
    new_res_dir = os.path.join(new_dir, BACKUP_DIR, BACKUP_RES_DIR)
    new_libs_dir = os.path.join(new_dir, BACKUP_DIR, BACKUP_LIBS_DIR)
    old_res_dir = os.path.join(old_dir, BACKUP_DIR, BACKUP_RES_DIR)
    old_libs_dir = os.path.join(old_dir, BACKUP_DIR, BACKUP_LIBS_DIR)
    diff_dir = os.path.join(new_dir, DIFF_DIR, old_version_dir, DIFF_UPDATE_DIR)

    def trimslash(f):
        if not f:
            return f
        f = f[1:] if f[0] == '/'  else f
        if not f:
            return f
        f = f[:-1] if f[-1] == '/' else f
        return f

    def map_file(f, dirtype):
        if dirtype == 'res':
            assert f.startswith(new_res_dir)
            return os.path.join(old_res_dir, trimslash(f[len(new_res_dir):]))
        elif dirtype == 'lib':
            assert f.startswith(new_libs_dir)
            return os.path.join(old_libs_dir, trimslash(f[len(new_libs_dir):]))

    changed = []
    allfiles = []
    scanFileList(new_res_dir, allfiles)
    for f in allfiles:
        res = genFileUpdate(f, map_file(f, 'res'), diff_dir)
        if res:
            res = {
                'action': res[0],
                'type': 'res',
                'old_file': trimslash(f[len(new_res_dir):]),
                'src_md5': res[1],
                'patch_md5': res[2],
                'md5': res[3],
                'new_file': res[4],
            }
            changed.append(res)
            Log.info('handled file[%s, %s]: %s', 'res', res['old_file'], json.dumps(res))


    allfiles = []
    scanFileList(new_libs_dir, allfiles)
    for f in allfiles:
        res = genFileUpdate(f, map_file(f, 'lib'), diff_dir)
        if res:
            res = {
                'action': res[0],
                'type': 'lib',
                'old_file': trimslash(f[len(new_libs_dir):]),
                'src_md5': res[1],
                'patch_md5': res[2],
                'md5': res[3],
                'new_file': res[4],
            }
            changed.append(res)
            Log.info('handled file[%s, %s]: %s', 'lib', res['old_file'], json.dumps(res))

    write2File(os.path.join(diff_dir, DIFF_RESULT_FILE), {
        'version': version,
        'files_to_update': changed
    })
    shutil.make_archive(diff_dir, 'zip', diff_dir)
    return changed


def genUpdates(new_version_dir, version):
    dirs = os.listdir(os.path.join(proj_path, env.INCUPDATE_DATA_DIR))
    old_version_dirs = [d for d in dirs \
        if os.path.isdir(os.path.join(proj_path, env.INCUPDATE_DATA_DIR, d)) \
            and isbackupdir(d)]
    Log.info('found old versions: %s', old_version_dirs)
    for old_version_dir in old_version_dirs:
        genUpdate(new_version_dir, old_version_dir, version)


def help():
    print 'Usage: ./zip_update.py /path/to/client_proj [--percent_limit=10] [--limit_key=value1] [--limit_key=value2] ...'
    print '       limit_key: one of', supported_limit_keys
    print '       percent_limit: integer of (0, 100)'


if __name__ == '__main__':
    if(len(sys.argv) < 2):
        help()
        sys.exit(1)
    proj_path = sys.argv[1]
    package_name = getPackageName(proj_path)
    env.INCUPDATE_DATA_DIR = os.path.join(env.INCUPDATE_DATA_DIR, package_name)
    if not os.path.isdir(os.path.join(proj_path, env.INCUPDATE_DATA_DIR)):
        os.makedirs(os.path.join(proj_path, env.INCUPDATE_DATA_DIR))
    parse_limit(sys.argv[2:])
    import update_client_res_version
    version, files, file_versions = update_client_res_version.genIncUpdateConfFile(proj_path)
    if find_path(proj_path, version):
        raise Exception('Already done zip_update!');
    dirname = backup(version)
    genUpdates(dirname, version)
    update_limit(proj_path, version, sys.argv[2:])
    print 'target_dir:', os.path.join(proj_path, env.INCUPDATE_DATA_DIR, dirname)





