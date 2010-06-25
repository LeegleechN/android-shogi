# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := .

include $(CLEAR_VARS)

LOCAL_MODULE    := bonanza

LOCAL_CFLAGS := -DNDEBUG -DMINIMUM -DNO_LOGGING -DANDROID -DNO_STDOUT
LOCAL_LDLIBS := -llog

LOCAL_SRC_FILES := data.c io.c proce.c utility.c ini.c attack.c book.c \ makemove.c unmake.c time.c csa.c valid.c bitop.c iterate.c searchr.c \ search.c quiesrch.c evaluate.c swap.c  hash.c root.c next.c movgenex.c \
genevasn.c gencap.c gennocap.c gendrop.c mate1ply.c rand.c learn1.c \
learn2.c evaldiff.c problem.c ponder.c thread.c sckt.c debug.c mate3.c \
      genchk.c JavaInterface.c
include $(BUILD_SHARED_LIBRARY)
