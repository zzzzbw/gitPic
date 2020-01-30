# gitPic

## gitPic,利用Github做图床小工具

### 环境需求

* Java 11
* git

### 依赖

* [Jfoenix](https://github.com/jfoenixadmin/JFoenix)
* [jGit](https://github.com/eclipse/jgit)

### 快速使用

1. 首先要有一个github账号，然后创建一个respository。

   ![Step1](https://raw.githubusercontent.com/zzzzbw/blog_source/master/images/GitPic/step1.png)

2. 给这个respository起一个名字,我们这里取名为git_resource。然后可以在Desciption中填写一些介绍。接下来勾选`Initialize this repository with a README`。最后点击`Create repository`。**注意respository要为Public，不然生成的链接会404**

   ![Step2](https://raw.githubusercontent.com/zzzzbw/blog_source/master/images/GitPic/step2.png)

3. clone项目到本地`git clone git@github.com:zzzzbw/git_resource.git --depth=1`。

    注意选择是https方式还是ssh方式，如果已经配置好ssh方式的话建议用这种方式，因为https需要输入账号密码。

   ![Step3](https://raw.githubusercontent.com/zzzzbw/blog_source/master/images/GitPic/step3.png)

4. 打开gitPic软件,在jar包目录下执行命令`java -jar gitPic.jar`。

5. 在gitPic中选择你要作为图床的git项目，在本案例中就是刚才创建的git_resource(选择后会读取该项目下的git信息,获取会花一点时间)，然后再选择要保存图片的文件夹，比如你的java系列的图片可以放在git_resource项目下的java文件夹下。

   ![Step4](https://raw.githubusercontent.com/zzzzbw/blog_source/master/images/GitPic/step4.png)

6. 拖拽图片或者点击选择图片来选择要上传的图片，gitPic会自动将该图片复制到git_resource/java文件夹下，并且生成对应的图片链接，而且该链接已经复制到你的剪贴版中了，可以直接黏贴到你的博文中了。

   ![Step5](https://raw.githubusercontent.com/zzzzbw/blog_source/master/images/GitPic/step5.png)

7. 只是此时这个链接实际上还没上传到github中，在浏览器中是无法访问的，这时候只要点提交并且上传等到上传成功后(如果之间是https模式clone的还要输入github的账号密码)，就可以在浏览器中访问了！

   ![Step6](https://raw.githubusercontent.com/zzzzbw/blog_source/master/images/GitPic/step6.png)

### 下载链接

[下载链接](https://github.com/zzzzbw/gitPic/releases)

如果有任何觉得需要改进的地方请留言或者在issue中提出，非常感谢！

### 开发者

由于在Java 11中去除了JavaFX组件，所以开发和之前版本的有所不同。

关于Java 11开发JavaFX可以查看[官方文档](https://openjfx.io/openjfx-docs/)

#### 添加JavaFX依赖

在`pom.xml`中引入JavaFX组件和`exec-maven-plugin`插件。

```xml
<dependencies>
    ...
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>11.0.2</version>
    </dependency>

    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>11.0.2</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.0</version>
            <configuration>
                <release>11</release>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>1.6.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>java</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <mainClass>com.zbw.gitpic.Bootstrap</mainClass>
            </configuration>
        </plugin>
    </plugins>
</build>
```

#### 运行程序

```shell
mvn compile exec:java
```

#### 打包程序

在`pom.xml`添加`maven-shade-plugin`打包插件

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.2.0</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <shadedArtifactAttached>true</shadedArtifactAttached>
                <shadedClassifierName>project-classifier</shadedClassifierName>
                <outputFile>shade\${project.artifactId}.jar</outputFile>

                <filters>
                    <filter>
                        <artifact>*:*</artifact>
                        <excludes>
                            <exclude>META-INF/*.SF</exclude>
                            <exclude>META-INF/*.DSA</exclude>
                            <exclude>META-INF/*.RSA</exclude>
                        </excludes>
                    </filter>
                </filters>
                <transformers>
                    <transformer implementation=
                                 "org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>com.zbw.gitpic.Launcher</mainClass>
                    </transformer>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```

运行命令打包

```java
mvn compile package
```

运行命令启动程序

```shell
java -jar shade/gitPic.jar
```