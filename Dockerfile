#
# Sbt Dockerfile
#
# https://github.com/dockerize/sbt
#

# Pull base image
FROM williamyeh/sbt 

MAINTAINER kenneth.lee@gmail.com

COPY . /app

