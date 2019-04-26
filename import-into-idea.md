以下在IntelliJ IDEA 2019.1、Gradle 5.4中测试通过

## 步骤

*进入到本地克隆的watchdog项目的工作目录中：*

1. 使用 `./gradlew :samples:user_center:compileJava` 对 `samples:user_center` 进行预编译 
2. 导入进IntelliJ  (File -> New -> Project from Existing Sources -> 找到该目录并选择 `build.gradle`)
3. 设置：将IntelliJ 的build/run 操作委托给Gradle（File -> Settings -> Build, Execution, Deployment -> Gradle -> Runner -> 右边的选项卡中勾选Delegate IDE build/run actions to gradle）
4. 可以继续编码了

## 提示

无论如何，请不要检入您自己生成的`.iml`、`.ipr`或`.iws`文件。您将注意到这些文件已经有意地被包含在`.gitignore`中了。