/****************************************************************************
Copyright (c) 2010 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
#include "CCFileUtilsAndroid.h"
#include "support/zip_support/ZipUtils.h"
#include "platform/CCCommon.h"
#include "ccMacros.h"
#include "jni/Java_org_cocos2dx_lib_Cocos2dxHelper.h"
#include <jni.h>
#include <jni/JniHelper.h>

using namespace std;

NS_CC_BEGIN


// changed to add incupdate functionality
static string s_assetsPath = "";
static string s_resDirRootPath = "";
static bool s_useAssets = false;

extern "C" {

    JNIEXPORT void JNICALL Java_org_cocos2dx_lib_Config_initPath(JNIEnv* env, jobject thiz, jboolean useAssets, jstring resPath) {
        s_useAssets = (bool)(useAssets != JNI_FALSE);
        CCLOG("debug-incupdate: useAssets=%d, %d, %d", (int)useAssets, JNI_TRUE, JNI_FALSE);
        if(s_useAssets) {
            s_assetsPath = JniHelper::jstring2string(resPath);
            CCLOG("debug-incupdate: assetsPath=%s", s_assetsPath.c_str());
        } else {
            string s = JniHelper::jstring2string(resPath);
            CCLOG("debug-incupdate: resDirRootPath0=%s", s.c_str());
            s_resDirRootPath = JniHelper::jstring2string(resPath);
            CCLOG("debug-incupdate: resDirRootPath=%s", s_resDirRootPath.c_str());
        }
    }

}
// changed to add incupdate functionality



// record the zip on the resource path
static ZipFile *s_pZipFile = NULL;

CCFileUtils* CCFileUtils::sharedFileUtils()
{
    if (s_sharedFileUtils == NULL)
    {
        s_sharedFileUtils = new CCFileUtilsAndroid();
        s_sharedFileUtils->init();
        std::string resourcePath = getApkPath();
        //s_pZipFile = new ZipFile(resourcePath, "assets/");

        // changed to add incupdate functionality
        if(s_useAssets) {
            s_pZipFile = new ZipFile(resourcePath, s_assetsPath.c_str());
        }
        // changed to add incupdate functionality
    }
    return s_sharedFileUtils;
}

CCFileUtilsAndroid::CCFileUtilsAndroid()
{
}

CCFileUtilsAndroid::~CCFileUtilsAndroid()
{
    CC_SAFE_DELETE(s_pZipFile);
}

bool CCFileUtilsAndroid::init()
{
    //m_strDefaultResRootPath = "assets/";

    // changed to add incupdate functionality
    if(s_useAssets) {
        m_strDefaultResRootPath = s_assetsPath;
    } else {
        m_strDefaultResRootPath = s_resDirRootPath;
    }
    // changed to add incupdate functionality

    return CCFileUtils::init();
}

bool CCFileUtilsAndroid::isFileExist(const std::string& strFilePath)
{
    if (0 == strFilePath.length())
    {
        return false;
    }

    bool bFound = false;

    // changed to add incupdate functionality
    std::string strPath = strFilePath;
    if (strPath[0] != '/' && !s_useAssets)
    {
        strPath.insert(0, s_resDirRootPath);
        CCAssert(strPath[0] == '/', "CCFileUtilsAndroid: should be an absolute path when fixed for incupdate.");
    }
    // changed to add incupdate functionality


    // Check whether file exists in apk.
    if (strPath[0] != '/')
    {
        if (strPath.find(m_strDefaultResRootPath) != 0)
        {// Didn't find "assets/" at the beginning of the path, adding it.
            strPath.insert(0, m_strDefaultResRootPath);
        }

        if (s_pZipFile->fileExists(strPath))
        {
            bFound = true;
        } 
    }
    else
    {
        CCLOG("strPath: %s", strPath.c_str());
        FILE *fp = fopen(strPath.c_str(), "r");
        if(fp)
        {
            bFound = true;
            fclose(fp);
        }
    }
    return bFound;
}

bool CCFileUtilsAndroid::isAbsolutePath(const std::string& strPath)
{
    // On Android, there are two situations for full path.
    // 1) Files in APK, e.g. assets/path/path/file.png
    // 2) Files not in APK, e.g. /data/data/org.cocos2dx.hellocpp/cache/path/path/file.png, or /sdcard/path/path/file.png.
    // So these two situations need to be checked on Android.
    if (strPath[0] == '/' || strPath.find(m_strDefaultResRootPath) == 0)
    {
        return true;
    }
    return false;
}


unsigned char* CCFileUtilsAndroid::getFileData(const char* pszFileName, const char* pszMode, unsigned long * pSize)
{    
    return doGetFileData(pszFileName, pszMode, pSize, false);
}

unsigned char* CCFileUtilsAndroid::getFileDataForAsync(const char* pszFileName, const char* pszMode, unsigned long * pSize)
{
    return doGetFileData(pszFileName, pszMode, pSize, true);
}

unsigned char* CCFileUtilsAndroid::doGetFileData(const char* pszFileName, const char* pszMode, unsigned long * pSize, bool forAsync)
{
    unsigned char * pData = 0;
    
    if ((! pszFileName) || (! pszMode) || 0 == strlen(pszFileName))
    {
        return 0;
    }
    
    string fullPath = fullPathForFilename(pszFileName);
    
    if (fullPath[0] != '/')
    {

        // changed to add incupdate functionality
        CCAssert(s_useAssets, "CCFileUtilsAndroid: must s_useAssets when fullPath is relative.");
        // changed to add incupdate functionality

        CCLOG("GETTING FILE assets DATA: %s, fullPath: %s", pszFileName, fullPath.c_str());
        if (forAsync)
        {
            pData = s_pZipFile->getFileData(fullPath.c_str(), pSize, s_pZipFile->_dataThread);
        }
        else
        {
            pData = s_pZipFile->getFileData(fullPath.c_str(), pSize);
        }
    }
    else
    {
        do
        {
            // read rrom other path than user set it
	        CCLOG("GETTING FILE ABSOLUTE DATA: %s, fullPath: %s", pszFileName, fullPath.c_str());
            FILE *fp = fopen(fullPath.c_str(), pszMode);
            CC_BREAK_IF(!fp);
            
            unsigned long size;
            fseek(fp,0,SEEK_END);
            size = ftell(fp);
            fseek(fp,0,SEEK_SET);
            pData = new unsigned char[size];
            size = fread(pData,sizeof(unsigned char), size,fp);
            fclose(fp);
            
            if (pSize)
            {
                *pSize = size;
            }
        } while (0);
    }
    
    if (! pData)
    {
        std::string msg = "Get data from file(";
        msg.append(pszFileName).append(") failed!");
        CCLOG("%s", msg.c_str());
    }
    
    return pData;
}

string CCFileUtilsAndroid::getWritablePath()
{
    // Fix for Nexus 10 (Android 4.2 multi-user environment)
    // the path is retrieved through Java Context.getCacheDir() method
    string dir("");
    string tmp = getFileDirectoryJNI();

    if (tmp.length() > 0)
    {
        dir.append(tmp).append("/");

        return dir;
    }
    else
    {
        return "";
    }
}

NS_CC_END
