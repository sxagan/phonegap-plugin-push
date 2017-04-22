#phonegap-plugin-push [![Build Status](https://travis-ci.org/phonegap/phonegap-plugin-push.svg)](https://travis-ci.org/phonegap/phonegap-plugin-push)

> Register and receive push notifications

# What is this?

This plugin offers support to receive and handle native push notifications with a **single unified API**. 

Starting with version `1.9.0`, this plugin will support `CocoaPods` installation of the `Google Cloud Messaging` library. More details are available in the [Installation](docs/INSTALLATION.md#cocoapods) documentation.

- [Reporting Issues](docs/ISSUES.md)
- [Installation](docs/INSTALLATION.md)
- [API reference](docs/API.md)
- [Typescript support](docs/TYPESCRIPT.md)
- [Examples](docs/EXAMPLES.md)
- [Platform support](docs/PLATFORM_SUPPORT.md)
- [Cloud build support (PG Build, IntelXDK)](docs/PHONEGAP_BUILD.md)
- [Push notification payload details](docs/PAYLOAD.md)
- [Contributing](.github/CONTRIBUTING.md)
- [License (MIT)](MIT-LICENSE)


# Do you like tutorial? You get tutorial!

 - [PhoneGap Day US Push Workshop 2016 (using node-gcm)](http://macdonst.github.io/push-workshop/)
 - [PhoneGap Day EU Push Workshop 2016 (using PhoneGap Push)](http://macdonst.github.io/push-workshop-eu/)

 # New Endpoints
```
PushNotification.scheduleReminder(function(res) {
    console.warn('res', res);
    // res = ok
}, function(err) {
    console.warn('err', err);
}, {
    itemId: itemId, // post UUID
    msg: msg, // msg to display in notification
    timestamp: timestamp // new Date().toJSON()
})

PushNotification.deleteReminder(function(res) {
    console.warn('res', res);
    // res = ok
}, function(err) {
    console.warn('err', err);
}, {
    itemId: itemId // post UUID
})

PushNotification.viewReminders(function(res) {
    console.warn('res', res);
    // res will be an object with UUID of post as key and the time of reminder set in milliseconds
    // e.g.
    // { uuid: 12321312321 }
}, function(err) {
    console.warn('err', err);
})

PushNotification.clearReminders(function(res) {
    console.warn('res', res);
    // OK
}, function(err) {
    console.warn('err', err);
})
```
