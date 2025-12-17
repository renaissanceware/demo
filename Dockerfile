# 第一阶段：构建阶段
FROM maven:3.8.8-eclipse-temurin-17 AS build

# 设置工作目录
WORKDIR /app

# 复制pom.xml文件并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制源代码并构建项目
COPY src ./src
RUN mvn clean package -DskipTests

# 第二阶段：运行阶段
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 从构建阶段复制构建好的jar文件
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# 暴露应用端口（Spring Boot默认端口为8080）
EXPOSE 8080

# 设置容器启动命令
CMD ["java", "-jar", "app.jar"]