# FSET Video Interview Gateway

[![Build Status](https://travis-ci.org/hmrc/fset-video-interview-gateway.svg)](https://travis-ci.org/hmrc/fset-video-interview-gateway) [ ![Download](https://api.bintray.com/packages/hmrc/releases/fset-video-interview-gateway/images/download.svg) ](https://bintray.com/hmrc/releases/fset-video-interview-gateway/_latestVersion)

Launchpadrecruits.com is a company that the FSET team use to present candidates with a "video interview". The Video Interview Gateway is the component responsible for communicating with Launchpad. FSET will use the gateway to send information to Launchpad as well as receive information from them via API calls.

## Application Configuration

references.conf contains non-sensitive default values and templates. For this application to function you should create an _application.conf_ in the conf/ directory. This file should look like:

```HOCON
microservice {
 services {
   launchpad {
     api {
       baseUrl = "BASEURLHERE"
       key = "KEYHERE"
       accountId = ACCOUNTIDHERE
       extensionValidUserEmailAddress = "valid.launchpad.user@address.com"
     }
   }
 }
}
```

If you're on the FSET team you can get these values from a colleague. Everything marked REPLACEME or 000000000 in _reference.conf_ should feature here.

### Contributing

Please get in touch with any of the contributing members if you would be interested in becoming a contributor. It is generally considered a good idea to get in touch before raising a pull request.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
 
