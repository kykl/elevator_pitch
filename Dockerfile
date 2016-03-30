#
# Sbt Dockerfile
#
# https://github.com/dockerize/sbt
#

# Pull base image
FROM dockerize/sbt

MAINTAINER kenneth.lee@gmail.com

COPY . .

# Default command
CMD ["sbt", "test"]
