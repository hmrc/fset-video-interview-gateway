#!/bin/bash

sbt -jvm-debug 7296 -J-Dplay.http.router=testOnlyDoNotUseInAppConf.Routes -Dhttp.port=9296
