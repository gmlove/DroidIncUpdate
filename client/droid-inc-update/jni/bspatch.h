//
//  bspatch.h
//  GCBSDiffKit
//
//  Created by Eric Entin on 8/4/11.
//  Copyright 2011 Group Commerce. All rights reserved.
//

#ifndef GCBSDiffKit_bspatch_h
#define GCBSDiffKit_bspatch_h

#ifdef __cplusplus
extern "C" {
#endif

int bspatch(const char *old_file, const char *new_file, const char* patch_file);

#ifdef __cplusplus
}
#endif

#endif
