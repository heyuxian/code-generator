

# Code Generator

## 项目简介

`CodeGenerator`，是一款用于`Intellj IDEA`的代码生成器插件，可以根据数据库实体对象(Entity/Domain)，通过自定义或是使用默认的模板，即可生成对应的`Java`代码。

## 快速使用

**下载插件**：[CodeGenerator](https://github.com/heyuxian/code-generator/releases)

**安装插件**：

![install](https://user-images.githubusercontent.com/30259465/32404191-17f1a142-c186-11e7-8da0-193d166224d8.jpg)



**配置模板**：

![settings](https://user-images.githubusercontent.com/30259465/32404202-3f8fc0da-c186-11e7-96e0-8f9ef64de971.jpg)

模板使用 `velocity` 作为代码生成引擎，相关文档请查看 [velocity user guide](http://velocity.apache.org/engine/devel/user-guide.html)

在 `velocity` 模板中 ，可以使用以下的变量：

- **entity** 源码[Entity](https://github.com/heyuxian/code-generator/blob/master/src/main/java/me/javaroad/plugins/model/Entity.java)
  - **name** entity class name
  - **packageName**  entity class package name
  - **fields** 
    - **name** 
    - **type**
  - **uncapital**() 将单词转换为复数形式
  - **plural**()  将entity class name首字母转换成小写
  - **uncapitalAndPlural**() 将entity class name首字母转换成小写且将单词转换为复数形式
- **basePackage** 用户选择的包名
- **YEAR** 当前的年份
- **TIME** 当前日期
- **USER** 当前用户

**示例**

[Article](https://github.com/heyuxian/mcloud/blob/master/mcloud-blog/src/main/java/me/javaroad/blog/entity/Article.java)

- **${entity.name}** => `Article`

- **${entity.packageName}** => `me.javaroad.blog.entity`

- **${entity.files}** =>

  - **name** => `title`
  - **type** => `String`

  ...

- **${entity.uncapital()}** => `Articles`

- **${entity.uncapitalAndPlural()}** => `articles`

- **${entity.plural()}** => `article`




**使用插件**

![generator](https://user-images.githubusercontent.com/30259465/32404457-d3f0b1ae-c18a-11e7-99e1-3ac8e838c30e.gif)



## 注意事项

插件默认的模板只适用于 [MCloud](https://github.com/heyuxian/mcloud) 项目，你可以根据默认模板进行修改;其次，本项目部分代码参考了 [CodeMaker]( https://github.com/x-hansong/CodeMaker) ，在此感谢插件作者。


## 问题及建议

若是对于本项目有任何问题或建议,可以在github上提Issue,如果是码云，可以直接发表评论,提Issue或是直接发私信给我.同时,如果你愿意参与开发,欢迎提PR.最后,如果你觉得本项目对你有所帮助，请点赞支持.

## License

```
Copyright 2017 http://www.javaroad.me

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
