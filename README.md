# FLogger [![](https://jitpack.io/v/sergyabanzhy/FLogger.svg)](https://jitpack.io/#sergyabanzhy/FLogger)
This is a one more Tree for Timber with log into file functionality.
All write operations are performed in a separate thread.
Once the log file reaches max specified size, a new one will be generated.

# How to add:

To get a Git project into your build:

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.sergyabanzhy:FLogger:1.0'
	}

To collect logs into a file:

	Timber.plant(
            FileTree.Builder()
            .filesDir(filesDir)
            .size(1024*1024)
            .build())

To print logs in LogCat and collect into a file:

	Timber.plant(
            FileTree.Builder()
            .filesDir(filesDir)
            .size(1024*1024)
            .build(), Timber.DebugTree())

To get log files:

	FileTree.getLogFiles {
		//log files will be there
	}

To use LogWriter in our own logger implementation:

	logWriter.queueEntryLog(EntryLog(tag, message, throwable))

How the log file looks like:

	2020-01-23 13:51:25.995 [MainActivity] renderState, Idle
	2020-01-23 13:51:45.410 [ActionExecutor] executeAction
	2020-01-23 13:51:45.413 [MainActivity] renderState, Fetching
	2020-01-23 13:51:45.417 [Repo] doing doJob 
	2020-01-23 13:51:47.422 [Repo] finished doJob
	2020-01-23 13:51:47.437 [ActionExecutor] executeAction
	2020-01-23 13:51:47.447 [MainActivity] renderState, Fetching2
	

# License
Copyright 2020 Serg Yabanzhy
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
