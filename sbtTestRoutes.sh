#!/bin/bash

sbt -jvm-debug 7296 -J-Dapplication.router=testOnlyDoNotUseInAppConf.Routes -Dhttp.port=9296 -Dplay.filters.headers.contentSecurityPolicy='www.google-analytics.com'
