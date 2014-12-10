#ifndef __HELLOWORLD_FILE_OPERATION__
#define __HELLOWORLD_FILE_OPERATION__

#include <string>
#include "CCStdC.h"

class FileOperation 
{
public:
	static void saveFile(void);
	static void readFile(void);
    static cocos2d::CCArray* readFileByLine(const std::string& fileName);
	static std::string getFilePath();
};

#endif
