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
from env import BASE_DIR, LIBS_DIR, RES_DIR, INC_UPDATE_FILE_PATH, \
scanFileList, genFileMD5, write2File, Log, BACKUP_DIR, \
BACKUP_LIBS_DIR, BACKUP_RES_DIR, DIFF_DIR, DIFF_UPDATE_DIR, PATCH_FILE_MIN_SIZE, \
PATCH_FILE_MAX_SIZE_RATIO, DIFF_RESULT_FILE, FILE_VERSIONS_FILE_PATH, UPDATE_LIMIT_FILE


limit_templ = {
  "prop_limit": {
    "cc": [
      "USA",
      "CN"
    ],
    "lc": [
      "eng",
      "zh"
    ],
    "tz": [
      "Asia/Shanghai"
    ],
    "did": [
      "5284047f4ffb4e04824a2fd1d1f0cd62"
    ],
    "m": [
      "sdk"
    ],
    "vr": [
      "4.0.4"
    ],
    "vsdk": [
      15
    ],
    "sc": [
      "320x256"
    ],
    "version": [
      "0730ad63be4833eb9f42fd6077ee34b4"
    ]
  },
  "percent_limit": 10
}

supported_limit_keys = limit_templ['prop_limit'].keys() + ['percent_limit'];


def help():
    print 'Usage: ./update_limit.py  /path/to/client_proj version [--percent_limit=10] [--limit_key=value1] [--limit_key=value2] ...'
    print '       limit_key: one of', supported_limit_keys
    print '       percent_limit: integer of (0, 100)'


def parse_limit(limitargs):
    r = re.compile('^--(' + '|'.join(supported_limit_keys) + ')=(.*)$')
    limit = {}
    for arg in limitargs:
        m = r.match(arg)
        if not m:
            print 'Incorrect parameter:', arg
            help()
            sys.exit(1)
        k, v = m.groups()
        if k == 'percent_limit':
            limit['percent_limit'] = int(v)
            continue
        if 'prop_limit' not in limit: limit['prop_limit'] = {}
        if k in limit['prop_limit']:
            if v not in limit['prop_limit'][k]:
                limit['prop_limit'][k].append(v)
        else:
            limit['prop_limit'][k] = [v]
    return limit


def find_path(proj_path, version):
    dirs = os.listdir(os.path.join(proj_path, env.INCUPDATE_DATA_DIR))
    from zip_update import isbackupdir
    all_dirs = [d for d in dirs \
        if os.path.isdir(os.path.join(proj_path, env.INCUPDATE_DATA_DIR, d)) \
            and isbackupdir(d)]
    targetdir = filter(lambda d: d.endswith('-' + version), all_dirs)
    return os.path.join(proj_path, env.INCUPDATE_DATA_DIR, targetdir[0]) if len(targetdir) else None


def update_limit(proj_path, version, limitargs):
    limit = parse_limit(limitargs)
    targetdir = find_path(proj_path, version)
    if not targetdir:
        raise Exception('Target version not found:' + version)
    targetfile = os.path.join(targetdir, UPDATE_LIMIT_FILE)
    write2File(targetfile, limit)
    print json.dumps(limit, indent=4)


if __name__ == '__main__':
    if(len(sys.argv) < 3):
        help()
        sys.exit(1)
    proj_path = sys.argv[1]
    package_name = getPackageName(proj_path)
    env.INCUPDATE_DATA_DIR = os.path.join(env.INCUPDATE_DATA_DIR, package_name)
    version = sys.argv[2]
    update_limit(proj_path, version, sys.argv[3:])


