#
# Sbt Dockerfile
#
# https://github.com/dockerize/sbt
#

# Pull base image
FROM dockerize/java:1.7

MAINTAINER Dockerize "http://dockerize.github.io"

COPY . .

# Install Sbt
RUN wget https://dl.bintray.com/sbt/debian/sbt-0.13.9.deb && dpkg -i sbt-0.13.9.deb

# Default command
CMD [sbt test]
