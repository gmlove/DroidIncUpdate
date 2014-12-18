import os
from os import path
import hashlib
import json
import pdb
import zipfile
from xml.dom import minidom


BASE_DIR = 'assets'
LIBS_DIR = path.join(BASE_DIR, 'incupdatelibs/')
RES_DIR = path.join(BASE_DIR, 'res')
INC_UPDATE_FILE_PATH = path.join(BASE_DIR, 'incupdate.conf')
FILE_VERSIONS_FILE_PATH = path.join(BASE_DIR, 'files.conf')

INCUPDATE_DATA_DIR = '../../incupdatedata'
BACKUP_DIR = 'backup'
# ../incupdatedata/backup/incupdatelibs
BACKUP_LIBS_DIR = 'incupdatelibs'
# ../incupdatedata/backup/res
BACKUP_RES_DIR = 'res'

DIFF_DIR = 'incupdate'
DIFF_UPDATE_DIR = 'update'
DIFF_RESULT_FILE = 'updateConf.json'

UPDATE_LIMIT_FILE = 'updatelimit.json'

(LOG_LEVEL_DEBUG, LOG_LEVEL_INFO, LOG_LEVEL_WARN, LOG_LEVEL_ERROR) = (1, 2, 3, 4)
LOG_LEVEL = LOG_LEVEL_INFO

#PATCH_FILE_MIN_SIZE = 1024 * 1024
PATCH_FILE_MIN_SIZE = 10
PATCH_FILE_MAX_SIZE_RATIO = 0.5

def scanFileList(path, result):
    pathList = os.listdir(path)
    for subPath in pathList:
        absPath = path + "/" + subPath
        result.append(absPath)
        if os.path.isdir(absPath):
            scanFileList(absPath, result)


def genFileMD5(files):
    if not isinstance(files, (list, tuple)):
        if(os.path.isdir(files)):
            return hashlib.md5(files).hexdigest()
        else:
            f = file(files, 'rb')
            md5 = hashlib.md5(f.read()).hexdigest()
            f.close()
            return md5

    result = []
    for filePath in files:
        if(os.path.isdir(filePath)):
            result.append(hashlib.md5(filePath).hexdigest())
        else:
            f = file(filePath, 'rb')
            result.append(hashlib.md5(f.read()).hexdigest())
            f.close()
    return result


def write2File(filePath, obj):
    f = open(filePath, 'w')
    f.write(json.dumps(obj, indent=4))
    f.close()
    print 'Write file OK[%s]!' % (filePath)


def getPackageName(projdir):
    xmldoc = minidom.parse(os.path.join(projdir, 'AndroidManifest.xml'))
    return xmldoc.getElementsByTagName('manifest')[0].attributes['package'].value



def debug_break():
    pdb.set_trace()


class Log(object):
    @staticmethod
    def warn(msg, *args):
        if LOG_LEVEL <= LOG_LEVEL_WARN:
            print '[WARN]', msg % args
    @staticmethod
    def info(msg, *args):
        if LOG_LEVEL <= LOG_LEVEL_INFO:
            print '[INFO]', msg % args
    @staticmethod
    def error(msg, *args):
        if LOG_LEVEL <= LOG_LEVEL_ERROR:
            print '[ERROR]', msg % args
    @staticmethod
    def debug(msg, *args):
        if LOG_LEVEL <= LOG_LEVEL_DEBUG:
            print '[DEBUG]', msg % args


