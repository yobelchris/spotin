<img src="https://bluemixassets.eu-gb.mybluemix.net/api/Products/image/logos/cloudant.svg?key=[starter-cloudant]&event=readme-image-view" alt="Cloudant Logo" width="200px"/>

## Cloudant
Bluemix Mobile Starter for Cloudant Sync in Java

[![](https://img.shields.io/badge/bluemix-powered-blue.svg)](https://bluemix.net)
[![](https://img.shields.io/badge/platform-android-lightgrey.svg?style=flat)](https://developer.android.com/index.html)

### Table of Contents
* [Summary](#summary)
* [Requirements](#requirements)
* [Configuration](#configuration)
* [Run](#run)
* [License](#license)

### Summary

The Cloudant Sync starter Android application shows how to do basic CRUD
(create, read, update, delete) with the local Datastore and how to
replicate between a remote Cloudant database and a local Datastore.

The application is a simple example of a "to-do" list with items which
can be created, marked "done", and deleted.

### Requirements

In order to run this starter, you will need the following:  

* Your Cloudant account username
* The name of your Cloudant database
* Cloudant API key
* Cloudant API password


To obtain these values:

1. Create a Cloudant instance tile and click the **LAUNCH** button from the Cloudant dashboard to open your Cloudant portal.
2. Once inside, select **Databases** from the left hand menu, then click **Create Database** at the top of the portal, name it whatever you'd like.
3. Once you have created a database on your Cloudant account for the application to
synchronize with, it's best-practice to use API keys for device access
rather than your Cloudant credentials. Select **Permissions** (or click the lock button <img src=PermissionsButton.png alt=PermissionsButton width="22" height="25"> next to your database) to get to the permissions screen.
4. Click **Generate API Key** and save the `Key` and `Password` values, you will need them later. Be sure to change the permissions for the `Key` you just created to include `read` and `write` access. `Admin` access is recommended to avoid any accessibility issues.
 * `read` and `write` permissions if you don't want to sync design documents.
 * `admin` permissions if you want to sync design documents.
5. Lastly, to get your Cloudant user name, grab the URL from your browser. The value before `.cloudant.com` is your user name. It should be a unique string with a `-bluemix` appended to the end. For example `f80a1a79-5d2f-4fe1-8ff3-f78abcad4fb1-bluemix`.

### Configuration

Replace the Strings in `res/values/cloudant_credentials.xml` with the values you gathered above:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="default_user">cloudant_account_name</string>
    <string name="default_dbname">example_app_todo</string>
    <string name="default_api_key">cloudant_api_key</string>
    <string name="default_api_password">cloudant_api_password</string>
</resources>
```

For example, ```<string name="default_user">cloudant_account_name</string>``` would be replaced with

```<string name="default_user">f80a1a79-5d2f-4fe1-8ff3-f78abcad4fb1-bluemix</string>```.

### Run

Now you are ready to build and run the sample application. You can run
the application on an emulator or an a development-enabled Android
device.

When the application starts, it will attempt to connect to your remote Cloudant database you created. Try adding a couple of tasks and hit "Upload (Push)" from the menu in the top right to add to the remote database. You should see these JSON documents appear in your Cloudant database. Changes to the documents in the Cloudant database will be replicated back to the device when you tap "Download (Pull)".

If you see "Replication Error" rather than "Replication Complete" as a popup message, check the logs to see more details on the exception.

If you'd like to enable more functionality provided by the Cloudant Sync sdk (like [encryption](https://github.com/cloudant/sync-android/blob/master/doc/encryption.md) or [conflict handling](https://github.com/cloudant/sync-android/blob/master/doc/conflicts.md)) see the [Cloudant Sync docs](https://github.com/cloudant/sync-android/tree/master/doc) for more learning.

### License
This package contains code licensed under the Apache License, Version 2.0 (the "License"). You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 and may also view the License in the LICENSE file within this package.
