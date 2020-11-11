#!/bin/bash

sbt -jvm-debug 7296 -J-Dplay.http.router=testOnlyDoNotUseInAppConf.Routes -Dhttp.port=9296 -Dplay.filters.headers.contentSecurityPolicy='www.google-analytics.com'
