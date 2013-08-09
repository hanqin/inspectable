inspectable
===========

A powerful tool that makes your android applications inspectable

---

###Usage

0. update android manifest, set the target project for inspection.
1. build inspectable (this project), install
    * ant debug; ant installd
2. adb shell am instrument me.hanqin.apps.inspect/me.hanqin.apps.inspect.Inspectable
    * Please take a look at the adb logcat output, you should find something like :
```
inetAddress.getHostAddress() = 192.168.56.101
```

###Try

Now, open you browser and try following urls:

* To browse all databases:     GET http://your-address:10086/database/

* To inspect a database:       GET http://your-address:10086/database/db_name.db

* To manipulate a database:    POST http://your-address:10086/database/db_name.db

                                  * with params set to => sql: your-sql-script

                                  * with optional param set to => bulk: true if it bulk operations, separated by ;



