FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu

ARG USER=appuser
ARG UID=1000
ARG GID=1000
RUN groupadd -g ${GID} ${USER} && \
    useradd -m -u ${UID} -g ${GID} -s /bin/bash ${USER}

USER ${USER}

WORKDIR /app

# Copy the built JAR file from the build stage to the current directory
COPY ./target/*.jar app.jar

# Set the entrypoint command for running the application
CMD ["java","-jar", "app.jar"]
