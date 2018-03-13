# gitPic

## gitPic,利用Github做图床小工具

### 环境需求

* java 8+（如果没有java环境也提供带jre版本的,只是体积就emmmm.......）
* git

### 依赖

* [Jfoenix](https://github.com/jfoenixadmin/JFoenix)
* [jGit](https://github.com/eclipse/jgit)

### 快速使用

1. 首先要有一个github账号，然后创建一个respository。

   ![Step1](https://raw.githubusercontent.com/zzzzbw/blog_source/master/images/GitPic/step1.png)

2. 给这个respository起一个名字,我们这里取名为git_resource。然后可以在Desciption中填写一些介绍。接下来勾选`Initialize this repository with a README`。最后点击`Create repository`。

   ![Step2](https://raw.githubusercontent.com/zzzzbw/blog_source/master/images/GitPic/step2.png)

3. clone项目到本地。注意选择是https方式还是ssh方式，如果已经配置好ssh方式的话建议用这种方式，因为https需要输入账号密码。

   ![Step3](https://raw.githubusercontent.com/zzzzbw/blog_source/master/images/GitPic/step3.png)

4. 打开gitPic软件,有java环境就在jar包目录下执行命令`java -jar gitPic-java.jar`,没有java环境就解压gitPic-exe.rar点击exe文件。

5. 在gitPic中选择你要作为图床的git项目，在本案例中就是刚才创建的git_resource(选择后会读取该项目下的git信息,获取会花一点时间)，然后再选择要保存图片的文件夹，比如你的java系列的图片可以放在git_resource项目下的java文件夹下。

   ![Step4](https://raw.githubusercontent.com/zzzzbw/blog_source/master/images/GitPic/step4.png)

6. 拖拽图片或者点击选择图片来选择要上传的图片，gitPic会自动将该图片复制到git_resource/java文件夹下，并且生成对应的图片链接，而且该链接已经复制到你的剪贴版中了，可以直接黏贴到你的博文中了。

   ![Step5](https://raw.githubusercontent.com/zzzzbw/blog_source/master/images/GitPic/step5.png)

7. 只是此时这个链接实际上还没上传到github中，在浏览器中是无法访问的，这时候只要点提交并且上传等到上传成功后(如果之间是https模式clone的还要输入github的账号密码)，就可以在浏览器中访问了！

   ![Step6](https://raw.githubusercontent.com/zzzzbw/blog_source/master/images/GitPic/step6.png)

### 下载链接

[下载链接](https://github.com/zzzzbw/gitPic/releases) (jar版本和exe版本)

如果有任何觉得需要改进的地方请留言或者在issue中提出，非常感谢！



