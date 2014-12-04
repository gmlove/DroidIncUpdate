#!/usr/bin/env python
# -*- coding: utf8 -*-
import sys, os
import json
import math
import string
import re
import datetime
import hashlib
import ConfigParser as cparser
from os import path
from env import BASE_DIR, LIBS_DIR, RES_DIR,INC_UPDATE_FILE_PATH, FILE_VERSIONS_FILE_PATH, \
scanFileList, genFileMD5, write2File, Log



def genIncUpdateConfFile(proj_dir):
    files = []
    files1 = []

    scanFileList(path.join(proj_dir, LIBS_DIR), files)
    files = [path.relpath(f, proj_dir) for f in files]
    size = 0
    file_sizes = {}
    for f in files:
        if not os.path.isdir(f):
            file_sizes[f] = os.path.getsize(f)
            size = size + file_sizes[f]
        else:
            file_sizes[f] = 4 * 1024
            size = size + file_sizes[f]
    #print 'file_sizes: ', json.dumps(file_sizes, indent=4)

    scanFileList(path.join(proj_dir, RES_DIR), files1)
    files1 = [path.relpath(f, proj_dir) for f in files1]
    size1 = 0
    file_sizes = {}
    for f in files1:
        if not os.path.isdir(f):
            file_sizes[f] = os.path.getsize(f)
            size1 = size1 + file_sizes[f]
        else:
            file_sizes[f] = 4 * 1024
            size1 = size1 + file_sizes[f]
    #print 'file_sizes: ', json.dumps(file_sizes, indent=4)

    files = files + files1
    files.sort();

    md5_list = genFileMD5(files)


    file_versions = dict([(files[i], md5_list[i]) for i in range(len(md5_list))]);
    print 'file_versions: ', json.dumps(file_versions, indent=4)
    write2File(path.join(proj_dir, FILE_VERSIONS_FILE_PATH), file_versions);

    str_to_md5 = '\n'.join([files[i] + ',' + md5_list[i] for i in range(0, len(files))])
    version = hashlib.md5(str_to_md5).hexdigest()
    print 'version:', version, 'libs_size:', size, 'res_size:', size1
    write2File(path.join(proj_dir, INC_UPDATE_FILE_PATH), {'version': version, 'libs_size': size, 'res_size': size1})
    return version, files, file_versions

def help():
    print 'Usage: ./update_client_res_version.py /path/to/client_proj'


if __name__ == '__main__':
    if(len(sys.argv) != 2):
        help()
        sys.exit(1)
    proj_path = sys.argv[1]
    genIncUpdateConfFile(proj_path)






