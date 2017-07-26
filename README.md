# 更新升级模块的使用

## 服务端
1. 服务端需要一个配置文件,其后缀名为"update.json",文件中是一个json字符串,其内容如下：
```json
{
	"version": "0.3", 
	"versionCode": "5",
	"url": "http://（IP+端口号）/mobile/Release/app-debug.apk", 
	"description": "1.修复部分bug"
}
```
* version字段：对应更新后apk文件的versionName。
* versionCode字段：对应更新后apk文件的versionCode，其数值需要大于更新前程序的versionCode才能完成更新。
* url字段：下载新apk的url地址。
* description字段：更新的内容描述。


## 手机移动端
1. 首先移动端项目导入更新模块library(updatelibs),并添加如下依赖：
```java
dependencies {
    compile project(':updatelibs')
}
```
2. 在项目module的AndroidManifest.xml文件中进行如下配置：
```xml
<application
 ...   
	>
    <activity android:name=".MainActivity"
        android:theme="@style/AppTheme">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />

            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
	
	<!--在项目module的AndroidManifest进行如下配置-->
    <provider
        android:authorities="${applicationId}.fileprovider"
        android:name="android.support.v4.content.FileProvider"
        android:grantUriPermissions="true"
        android:exported="false">
        <meta-data android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths"/>
    </provider>


</application>
```
3. 在项目的Application 的onCreate中进行如下初始化：
```java
//参数url设置为update.json文件路径
UpdateUtils.init(url);

```
例如，update文件在服务器的地址为"http://（IP+端口号）/update.json",那么参数url就设置为"http://（IP+端口号）/"

4. **检查更新**

应用更新升级，需要下载新的apk安装包，因此需要在程序中开启WRITE_EXTERNAL_STORAGE权限。在Android6.0以下版本时，仅仅在AndroidManifest中声明即可；在Android6.0及以上版本时，需要进行运行时权限的申请。

为了更好的兼容性，我们在启动程序界面Activity的onCreate方法中进行了如下操作：
```java

//判断是否已经授权WRITE_EXTERNAL_STORAGE权限
//如果没有授权WRITE_EXTERNAL_STORAGE权限
if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
    //程序启动时申请WRITE_EXTERNAL_STORAGE权限，设置requestPermissions方法参数requestCode为5
	int requestCode = 5;
	ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
//如果已经授权WRITE_EXTERNAL_STORAGE权限
} else {
	//检查更新，第三个参数为0
	UpdateUtils.checkUpdateInfo(getApplication(), this.getPackageName() + ".fileprovider", 0);
}
```
手动检查更新需要与启动检查更新的操作方法类似，不同的地方有两点，一是requestPermissions方法第三个参数requestCode数值与程序启动时的requestPermissions方法requestCode数值区分开，用来在权限请求回调方法中进行很好的区分；二是检查更新调用的checkUpdateInfo方法，第三个参数赋值为1。手动检查更新权限申请具体如下：
```java
if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
    //手动更新时申请WRITE_EXTERNAL_STORAGE权限，设置requestPermissions方法参数requestCode设置为6
	int requestCode = 6;
	ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
} else {
	//检查更新，第三个参数为1
	UpdateUtils.checkUpdateInfo(getApplication(), MainActivity.this.getPackageName() + ".fileprovider", 1);
}
```

WRITE_EXTERNAL_STORAGE权限的请求回调方法如下，如果请求权限通过则检查更新。
```java
//运行时权限请求回调方法
@Override
public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    switch (requestCode) {
        //程序启动时请求WRITE_EXTERNAL_STORAGE权限对应回调
        case 5:
            //如果权限请求通过
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //程序启动时检查更新，checkUpdateInfo方法第三个参数设置为0
                UpdateUtils.checkUpdateInfo(getApplication(), MainActivity.this.getPackageName() + ".fileprovider", 0);
            } else {
            }
            break;
        //手动检查更新时请求WRITE_EXTERNAL_STORAGE权限对应回调
        case 6:
            //如果权限请求通过
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //手动检查更新，checkUpdateInfo方法第三个参数设置为1
                UpdateUtils.checkUpdateInfo(getApplication(), MainActivity.this.getPackageName() + ".fileprovider", 1);
            } else {
            }
            break;
    }
}
```

具体使用方法可参考demo：[https://github.com/shufengwu/UpdateLibTest](https://github.com/shufengwu/UpdateLibTest "https://github.com/shufengwu/UpdateLibTest")
