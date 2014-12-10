
INC_TOOLS_DIR=../tools

update-binding:
	cd jni/ && cat cocosloader/cocos_binding | ${INC_TOOLS_DIR}/binding_gen.py > cocosloader/binding.c && ndk-build && cd -
