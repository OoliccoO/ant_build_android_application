
## 说明
该例子所运行的系统为macOS High Sierra,但对于window用户同样具有参考意义，下面笔者只针对macOS下的配置和使用作详细说明，window用户需自行进行ant等的配置。在例子中笔者不直接使用ant原有的build.xml（但是该文档有很多值得参考的地方，读者请自行研究），而是使用了一个自定义的build_mac.xml，里面详细的把整个构建过程分类说明，包含每个阶段需要使用到什么命令和命令如何调用，以及如何分包（解决[65535异常问题](http://androidxref.com/4.4_r1/xref/external/dexmaker/src/dx/java/com/android/dx/dex/file/MemberIdsSection.java)）都作了说明，对于学习如何使用ant构建android应用的初学者来说是会是一篇不错的文章。注意：该文章中使用到的项目程序是笔者随意找来作例子使用，项目的程序逻辑和书写风格等不在该文章讨论的范围。<br><br>

## 项目的结构
[主项目](https://github.com/OoliccoO/ant_build_android_application/tree/master/Test)<br>
>Test
>>assets (内含mp3资源)<br>
>>res (静态资源)<br>
>>libs<br>
>>>10k-methods.jar<br>
>>>classes2.jar<br>
>>>more60k.jar<br>
>>
>>src (源码目录)<br>
>>ant (内含跟生成dex文件所需的架包)<br>
>>AndroidManifest.xml (程序清单)<br>
>>ant.properties (暂无用)<br>
>>build_mac.xml (构建脚本)<br>
>>pathtool.jar (路径分类的架包)<br>
>>proguard-project.txt (暂无用)<br>
>>project.properties (暂无用)<br>
>
[库](https://github.com/OoliccoO/ant_build_android_application/tree/master/Library)<br>
>Library
>>assets (暂无用)<br>
>>res (静态资源)<br>
>>libs (第三方库)<br>
>>>classes1.jar<br>
>>
>>src (源码目录)<br>
>>AndroidManifest.xml (程序清单)<br>
>>ant.properties (暂无用)<br>
>>proguard-project.txt (暂无用)<br>
>>project.properties (暂无用)<br><br>

## macOS下配置和使用ant
1.ant下载，官方下载链接：http://ant.apache.org/bindownload.cgi, 笔者下载的版本为[1.10.3 .zip archive: apache-ant-1.10.3-bin.zip](http://mirror.bit.edu.cn/apache//ant/binaries/apache-ant-1.10.3-bin.zip)，下载完毕后解压到一个任意不含中文的路径即可。<br>
2.配置ant环境变量(这里只是参考，读者可以按照网上其他方式配置，只要正常使用即可)<br>
(1).开启终端，获取root权限：$sudo -s，然后输入管理员密码（即开机密码）。<br>
(2).修改bashrc文件读写权限：chmod +w /etc/bashrc 。<br>
(3).修改bashrc文件：vi /etc/bashrc，按i键进入编辑状态，在文件末尾加入下面两行<br>
&nbsp;&nbsp;&nbsp;&nbsp;export ANT_HOME=/Users/Licc/Documents/Ant/apache-ant-1.10.3<br>
&nbsp;&nbsp;&nbsp;&nbsp;export PATH=${PATH}:${ANT_HOME}/bin<br>
按ESC键退出编辑状态。输入:wq!保存并退出。<br>
(4).测试是否配置成功，输入：ant -version，若显示Apache Ant(TM) version 1.10.3 compiled on March 24 2018说明配置成功，否则重复上述步骤。<br>
(5).如有必要，请配置Android sdk，否则在构建时部分sdk提供的命令无法正常使用，笔者的配置如下图示：<br>
<div width="100%" align="center">
  <img width=600px height=433px src="https://github.com/OoliccoO/ant_build_android_application/blob/master/image/03A7E00D-679F-413D-B188-3D8F30671B67.png"/>
</div><br>
(5).执行ant自动化构建的命令为：ant -buildfile xxxxxxx.xml，例如主项目中有一个名为build_mac的构建文件，在终端中使用命令进入到主项目中，然后执行：ant -buildfile build_mac.xml命令即可。<br><br>

## 构建Android应用
**说明：ant具体语法的使用不在该文章的讨论范围，文中只对部分使用到的标签进行说明，ant语法使用请翻阅专业读本或书籍。**<br>

1.快速上手：<br>
apache ant的简介和常用标签说明，请查阅[apache ant(百度百科)](https://baike.baidu.com/item/apache%20ant/1065741)。<br>

2.构建流程说明：<br>
* 生成用于主应用和使用到的库的R.java<br>
* 编译aidl为java，编译所有java文件为class文件<br>
* 打包class文件和jar包为classes.dex<br>
* 打包assets和res资源为资源压缩包(如res.zip，名字可以自己定义)<br>
* 组合classes.dex和res.zip生成未签名的APK<br>
* 生成有签名的APK<br>
<div width="100%" align="center">
  <img width=724px height=349px src="https://github.com/OoliccoO/ant_build_android_application/blob/master/image/7970ED6E-D8DA-4B96-9D55-EFA4DA25DA9E.png"/>
</div><br>
3.编写构建文件（build_mac.xml）：<br>
(1).设置全局属性,包括jdk目录，sdk目录以及签名时需要的key目录等。<br>

```
    <!-- 应用名称，版本号，版本名称等 -->
    <property name="appName" value="ant_packaging_release" />
    <property name="version-code" value="2" />
    <property name="version-name" value="1.1.0" />
    <property name="min-sdk-version" value="19" />
    <property name="target-sdk-version" value="21" />
    <property name="jar.app.name" value="main.jar"/>
    <!-- 签名证书文件(请使用自己的key) -->
    <property name="keystore-file" value="${basedir}/key.keystore" />
    <!-- SDK目录 -->
    <property name="sdk-folder" value="/Users/Licc/Library/Android/sdk" />
    <!-- SDK指定平台目录 -->
    <property name="sdk-platform-folder" value="${sdk-folder}/platforms/android-21" />
    <!-- SDK中tools目录 -->
    <property name="sdk-tools" value="${sdk-folder}/tools" />
    <!-- SDK指定平台中tools目录 -->
    <property name="sdk-platform-tools" value="${sdk-folder}/build-tools/27.0.3" />
    <!--<property name="sdk-platform-tools" value="${sdk-folder}/build-tools/21.1.2" />-->
    <!-- Android Asset Packaging Tool，Android资源打包工具 -->
    <property name="aapt" value="${sdk-platform-tools}/aapt" />
    <!-- Android Interface definition language，将aidl转为java -->
    <property name="aidl" value="${sdk-platform-tools}/aidl" />
    <!-- android将jar包转成dex格式 -->
    <property name="dx" value="${sdk-platform-tools}/dx" />
    <!-- 【★】旧SDK时使用apkbuilder生成apk -->
    <!-- <property name="apkbuilder" value="${sdk-tools}/apkbuilder" />-->
    <!-- jdk目录 -->
    <property name="jdk-folder" value="/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home" />
    <!-- jar 签名和校验工具 -->
    <property name="jarsigner" value="${jdk-folder}/bin/jarsigner" />
    <!-- 编译需要的平台android.jar -->
    <property name="android-jar" value="${sdk-platform-folder}/android.jar" />
    <!-- 编译aidl文件所需的预处理框架文件framework.aidl -->
    <property name="framework-aidl" value="${sdk-platform-folder}/framework.aidl" />
    <!-- 生成R文件的相对目录 -->
    <property name="outdir-gen" value="gen" />
    <!-- 编译后的文件放置目录（临时目录）-->
    <property name="outdir-bin" value="bin" />
    <!-- 发布目录-->
    <property name="outdir-release" value="release" />
    <!-- AndroidManifest.xml（清单文件）文件 -->
    <property name="manifest-xml" value="AndroidManifest.xml" />
    <!-- 静态资源文件目录 -->
    <property name="resource-dir" value="res" />
    <property name="asset-dir" value="assets" />
    <!-- java源文件目录 -->
    <property name="srcdir" value="src" />
    <property name="srcdir-ospath" value="${basedir}/${srcdir}" />
    <!-- 第三方架包 -->
    <property name="external-lib" value="libs" />
    <property name="external-lib-ospath" value="${basedir}/${external-lib}" />
    <!-- 存放class的主目录 -->
    <property name="outdir-classes" value="${outdir-bin}" />
    <property name="outdir-classes-ospath" value="${basedir}/${outdir-classes}" />
    <!-- classes.dex相关变量（目录和文件名） -->
    <property name="dex-file" value="classes_out.dex" />
    <property name="dex-path" value="${outdir-bin}/${dex-file}" />
    <property name="dex-ospath" value="${basedir}/${dex-path}" />
    <!-- 经过aapt生成的静态资源包文件 -->
    <property name="resources-package" value="${outdir-bin}/resources.zip" />
    <property name="resources-package-ospath" value="${basedir}/${resources-package}" />
    <!-- 引用到的库 -->
    <property name="library-dir1" value="/Users/Licc/Documents/work/data/ANT_Mac/TestAnt/Library" />
    <!-- 未签名的apk包信息 -->
    <property name="out-unsigned-package" value="${outdir-bin}/${appName}_unsigned.apk" />
    <property name="out-unsigned-package-ospath" value="${basedir}/${out-unsigned-package}" />
    <!-- 签名apk包信息 -->
    <property name="out-signed-package" value="${outdir-release}/${appName}.apk" />
    <property name="out-signed-package-ospath" value="${basedir}/${out-signed-package}" />
    <!-- ////////////////// 分包相关的设置（start） ////////////////// -->
    <!-- 注意，在Test项目中还包含了一个名为ant的文件夹，包含了分包过程中需要的jar -->
    <property name="zipalign" value="${sdk-platform-tools}/zipalign" />
    <!-- 提供测试数据：提供全部的jar的路径 -->
    <path id="project.all.jars.path">
        <fileset dir="${basedir}/libs" includes="*.jar"/>
        <fileset dir="${library-dir1}/libs" includes="*.jar"/>
    </path>
    <!-- 需要分包的jar路径集 -->
    <path id="out.dex.jar.assets"/>
    <path id="out.dex.jar.input.ref"/>
    <path id="pathtool.antlibs">
        <pathelement path="${basedir}/pathtool.jar" />
    </path>
    <taskdef resource="anttasks.properties" classpathref="pathtool.antlibs"/>
    <path id="android.antlibs">
        <pathelement path="${basedir}/ant/ant-tasks.jar" />
    </path>
    <taskdef resource="anttasks.properties" classpathref="android.antlibs" />
    <!-- ////////////////// 分包相关的设置（end） ////////////////// -->
```

设置platforms版本和build-tools版本时，需要版本匹配，否则会报UNEXPECTED TOP-LEVEL EXCEPTION。
<div width="100%" align="center">
  <img width=741px height=492px src="https://github.com/OoliccoO/ant_build_android_application/blob/master/image/FA30459D-3760-4632-8B30-65D2B2198F29.png"/>
</div><br>

(2).使用aapt命令生成R文件，包含了所有res（包含主项目和库）目录下资源的ID。注意，如果主项目中使用到了库中的静态资源的话，亦需要添加进来，否则将导致编译时出错，另外还需要关注就是不同项目中可能存在命名相同的资源，所以注意资源的覆盖。<br>

```
    <target name="gen-R" depends="init">
        <exec executable="${aapt}" failonerror="true">
            <arg value="package" />
            <arg value="-f" />
            <arg value="-m" />
            <arg value="-J" />
            <arg value="${outdir-gen}" />
            <arg value="-S" />
            <arg value="${resource-dir}" />
            <!-- 库资源 -->
            <arg value="-S" />
            <arg value="${library-dir1}/res" />
            <arg value="-M" />
            <arg value="${manifest-xml}" />
            <arg value="-I" />
            <arg value="${android-jar}" />
            <arg value="--auto-add-overlay" /> <!-- 覆盖资源-->
        </exec>
    </target>
```

(3).使用aidl命令编译aidl文件。<br>

```
    <target name="aidl" depends="gen-R">
        <apply executable="${aidl}" failonerror="true">
            <!-- 指定预处理文件 -->
            <arg value="-p${framework-aidl}" />
            <!-- aidl声明的目录 -->
            <arg value="-I${srcdir}" />
            <!-- 目标文件目录 -->
            <arg value="-o${outdir-gen}" />
            <!-- 指定哪些文件需要编译 -->
            <fileset dir="${srcdir}">
                <include name="*.aidl" />
            </fileset>
        </apply>
    </target>
```

(4).使用javac命令将工程中的java源文件编译成class文件。主项目中使用到库中src目录下的类时，需要使用sourcepath属性将库的src添加进来；
若主项目中使用到内部或外部jar时，亦需要使用classpath标签添加进来，否则编译过程中报类找不到或未定义的异常。<br>

```
    <target name="compile" depends="aidl">
        <javac bootclasspath="${android-jar}"
            compiler="javac1.8"
            destdir="${outdir-classes}"
            encoding="utf-8"
            srcdir="."
            includeantruntime="false"
            target="1.8">
            <!-- 库的源文件目录 -->
            <sourcepath path="${library-dir1}/src" />
            <classpath>
                <!-- 项目使用了自身libs中的jar时要添加进来 -->
                <fileset dir="${external-lib}" includes="*.jar" />
                <!-- 库的libs中的jar时要添加进来 -->
                <fileset dir="${library-dir1}/libs" includes="*.jar" />
            </classpath>
        </javac>
    </target>
```

(5).分包。感谢Tu Yimin分享一个开源项目：[Dex65536](https://github.com/mmin18/Dex65536)， 里面分享了一个如何借助ant进行分包的例子，从而解决低版本系统下的[65535异常问题](http://androidxref.com/4.4_r1/xref/external/dexmaker/src/dx/java/com/android/dx/dex/file/MemberIdsSection.java)，该项目分享了一种基于ant原生的build.xml,然后重写```<target name="-post-compile">```来实现分包；文章开篇时提过，笔者不直接使用build.xml,根据该开源项目的分包原理，笔者提取其核心内容来实现了分包。其中在主项目的ant目录下包含了分包过程中需要到的jar，如果笔者现在提供的jar在你的开发环境下无法使用时，请自行下载，其中核心jar为ant-tasks.jar(这个你也可以使用自己的)，其内部的MANIFEST.MF文件中有相关的Class-Path说明，补齐需要用到的jar即可。<br>

```
    <target name="-post-compile" depends="compile">
        <!-- 实际用于分类路径(可自行查看pathtool.jar的内容) -->
        <!--
        扼要说明：
        refid： 包含全部jar的路径
        includeRefid：需要分包的jar（路径）
        excludeRefid：不需要分包的jar（路径）
        -->
        <!--/classes2.jar,/classes1.jar,/10k-methods.jar-->
        <pathtool
            libs="/more60k.jar"
            excludeRefid="out.dex.jar.input.ref"
            includeRefid="out.dex.jar.assets"
            refid="project.all.jars.path" />
        <!-- 创建目录 -->
        <mkdir dir="${outdir-bin}/libs.apk" />
        <!-- 将需要分包的jar打包到dex中 -->
        <dex
            dexedlibs="${basedir}/${outdir-bin}/libs.apk"
            disableDexMerger="false"
            executable="${dx}"
            forceJumbo="false"
            nolocals="true"
            output="${outdir-bin}/libs.apk/classes.dex">
            <path refid="out.dex.jar.assets" />
        </dex>
        <!-- 压缩上步生成的dex文件（后缀为apk） -->
        <zip basedir="${outdir-bin}/libs.apk"
            destfile="${outdir-bin}/libs-unaligned.apk"
            includes="*.dex"
            update="true" />
        <!-- 优化上步生成的文件（优化apk）-->
        <zipalign
            executable="${zipalign}"
            input="${outdir-bin}/libs-unaligned.apk"
            output="${asset-dir}/libs.apk"
            verbose="${verbose}" />
    </target>
```

<br>分包完成后，还需要在java代码中将分包加载到应用中,否则运行时报错。其原理为：不违背类的委托加载机制，将PathClassLoader->BootClassLoader的委托关系改变成为PathClassLoader->DexClassLoader->BootClassLoader的关系。<br>

```
    @SuppressLint("NewApi")
    private void dexTool() {
        File dexDir = new File(getFilesDir(), "dlibs");
        dexDir.mkdir();
        File dexFile = new File(dexDir, "libs.apk");
        File dexOpt = getCacheDir();
        try {
            InputStream ins = getAssets().open("libs.apk");
            if (dexFile.length() != ins.available()) {
                FileOutputStream fos = new FileOutputStream(dexFile);
                byte[] buf = new byte[4096];
                int l;
                while ((l = ins.read(buf)) != -1) {
                    fos.write(buf, 0, l);
                }
                fos.close();
            }
            ins.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ClassLoader cl = getClassLoader();
        ApplicationInfo ai = getApplicationInfo();
        String nativeLibraryDir = null;
        if (Build.VERSION.SDK_INT > 8) {
            nativeLibraryDir = ai.nativeLibraryDir;
        } else {
            nativeLibraryDir = "/data/data/" + ai.packageName + "/lib/";
        }
        DexClassLoader dcl = new DexClassLoader(dexFile.getAbsolutePath(), 
        dexOpt.getAbsolutePath(), nativeLibraryDir, cl.getParent());
        try {
            // ClassLoader.class.getDeclaredField这里值得注意的，parent变量只存在在java.lang.ClassLoaderz
            Field f = ClassLoader.class.getDeclaredField("parent");
            f.setAccessible(true);
            f.set(cl, dcl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
```

(6).将需要打包到主包的classes打包到主包中（classes.dex）。这里需要注意的是，当你的应用进行了分包后，已经打包到分包的class无需再打到主包中。<br>

```
    <target name="dex" depends="-post-compile">
        <jar
            jarfile="${outdir-classes-ospath}/${jar.app.name}"
            basedir="${outdir-classes-ospath}">
            <include name="**/*.class"/>
        </jar>
        <dex
            dexedlibs="${basedir}/${outdir-bin}/libs.apk"
            disableDexMerger="false"
            executable="${dx}"
            forceJumbo="false"
            nolocals="true"
            output="${dex-ospath}">
            <path>
                <pathelement path="${outdir-classes-ospath}/main.jar"/>
            </path>
            <!-- 使用到的libs(无需打到分包中的) -->
            <path refid="out.dex.jar.input.ref" />
        </dex>
    </target>
```
(7).打包静态资源，将相关的assets，res和清单文件打包。<br>

```
    <target name="package-res-and-assets">
        <exec executable="${aapt}" failonerror="true">
            <arg value="package" />
            <arg value="-f" />
            <arg value="-M" />
            <arg value="${manifest-xml}" />
            <!-- 主应用的静态资源 -->
            <arg value="-S" />
            <arg value="${resource-dir}" />
            <arg value="-A" />
            <arg value="${asset-dir}" />
            <!-- 注意点:如果引用了库，同时需要调用Library的res/assets -->
            <arg value="-S" />
            <arg value="${library-dir1}/res" />
            <arg value="-A" />
            <arg value="${library-dir1}/assets" />
            <arg value="-I" />
            <arg value="${android-jar}" />
            <arg value="--version-code" />
            <arg value="${version-code}" />
            <arg value="--version-name" />
            <arg value="${version-name}" />
            <arg value="--min-sdk-version" />
            <arg value="${min-sdk-version}" />
            <arg value="--target-sdk-version" />
            <arg value="${target-sdk-version}" />
            <arg value="-F" />
            <arg value="${resources-package}" />
            <arg value="--auto-add-overlay" />   <!-- 覆盖资源 -->
        </exec>
    </target>
```

(8).打包apk，将dex文件和静态资源文件打包到apk中。<br>

```
    <target name="package" depends="dex, package-res-and-assets">

        <!-- 旧版SDK时使用apkbuilder生成apk -->
        <!-- <exec executable="${apkbuilder}" failonerror="true">-->
        <!-- <arg value="${out-unsigned-package-ospath}" />-->
        <!-- <arg value="-u" />-->
        <!-- <arg value="-z" />-->
        <!-- <arg value="${resources-package-ospath}" />-->
        <!-- <arg value="-f" />-->
        <!-- <arg value="${dex-ospath}" />-->
        <!-- <arg value="-rf" />-->
        <!-- <arg value="${srcdir-ospath}" />-->
        <!-- </exec>-->

        <!-- 新版SDK时使用sdklib.jar生成apk -->
        <java classname="com.android.sdklib.build.ApkBuilderMain"
            classpath="${sdk-tools}/lib/sdklib-26.0.0-dev.jar">
            <!-- 输出apk的路径 -->
            <arg value="${out-unsigned-package-ospath}" />
            <!-- u指创建未签名的包-->
            <arg value="-u" />
            <!-- 指定前资源压缩包路径res.zip文件路径 -->
            <arg value="-z" />
            <!-- 资源压缩包 -->
            <arg value="${resources-package-ospath}" />
            <arg value="-f" />
            <arg value="${dex-ospath}" />
        </java>
    </target>
```

(8).使用jarsigner对apk签名，由于笔者当前使用到的jdk版本为1.8，所以在签名时会提示时间戳的警告，而低版本的jdk未报该警告。当提示时间戳警告时，需要
添加“-tsa”属性即可。<br>

```
    <target name="jarsigner" depends="package">
        <exec executable="${jarsigner}" failonerror="true">
            <arg value="-keystore" />
            <arg value="${keystore-file}" />

            <!-- 没有-tsa此项会报时间戳的问题，如果当前网址不能用时，更换一个可用的时间戳认证网址即可 -->
            <arg value="-tsa" />
            <arg value="http://tsa.starfieldtech.com/" />

            <arg value="-digestalg" />
            <arg value="SHA1" />
            <arg value="-sigalg" />
            <arg value="MD5withRSA" />
            <!-- 密钥库密码 -->
            <arg value="-storepass" />
            <arg value="xxxxxxx" />
            <!-- 密钥密码 -->
            <arg value="-keypass" />
            <arg value="xxxxxxx" />
            <arg value="-signedjar" />
            <arg value="${out-signed-package-ospath}" />
            <arg value="${out-unsigned-package-ospath}" />
            <!-- 证书的别名 -->
            <arg value="key0" />
        </exec>
    </target>
```

## 联系邮箱
若需要咨询或问题反馈，请联系：**13750096351@163.com**<br><br>
