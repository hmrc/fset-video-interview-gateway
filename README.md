# FSET Launchpad Gateway

[![Build Status](https://travis-ci.org/hmrc/fset-launchpad-gateway.svg)](https://travis-ci.org/hmrc/fset-launchpad-gateway) [ ![Download](https://api.bintray.com/packages/hmrc/releases/fset-launchpad-gateway/images/download.svg) ](https://bintray.com/hmrc/releases/fset-launchpad-gateway/_latestVersion)

Launchpadrecruits.com is a company that the FSET team use to present candidates with a "video interview". The Launchpad Gateway is the component responsible for communicating with Launchpad. FSET will use the gateway to send information to Launchpad as well as receive information from them via API calls.

## Application Configuration

references.conf contains non-sensitive default values and templates. For this application to function you should create an _application.conf_ in the conf/ directory. This file should look like:

```HOCON
microservice {
 services {
   launchpad {
     api {
       baseUrl = "BASEURLHERE"
       key = "KEYHERE"
       accountId = "ACCOUNTIDHERE"
     }
   }
 }
}
```

If you're on the FSET team you can get these values from a colleague.

### Contributing

Please get in touch with any of the contributing members if you would be interested in becoming a contributor. It is generally considered a good idea to get in touch before raising a pull request.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")