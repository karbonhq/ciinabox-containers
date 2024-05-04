# Use an official Jenkins agent image as a parent image
FROM jenkins/agent

# Set the maintainer label
LABEL maintainer="Ameya Agashe <ameya.agashe@karbonhq.com>"

# Set environment variables
ENV DEBIAN_FRONTEND noninteractive

# Run all operations as root
USER root

# Update and install necessary packages
RUN apt-get update && apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg2 \
    lxc \
    iptables \
    git \
    bison \
    build-essential \
    zlib1g-dev \
    libssl-dev \
    libxml2-dev \
    git-core \
    git-flow \
    python3 \
    python3-pip \
    npm \
    locales \
    unzip \
    awscli \
    openssh-server  # Adding OpenSSH server

# Configure SSH daemon
RUN mkdir /var/run/sshd \
    && sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin no/' /etc/ssh/sshd_config \
    && sed -i 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' /etc/pam.d/sshd

# Configure locale
RUN echo "en_US.UTF-8 UTF-8" > /etc/locale.gen && \
    locale-gen en_US.UTF-8 && \
    update-locale LANG=en_US.UTF-8

# Environment settings for locale
ENV LC_ALL=en_US.UTF-8
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US.UTF-8

# Install Git LFS
RUN curl -s https://packagecloud.io/install/repositories/github/git-lfs/script.deb.sh | bash && \
    apt-get install -y git-lfs && \
    git lfs install

# Install ChefDK
RUN curl -L https://packages.chef.io/files/stable/chefdk/4.13.3/debian/10/chefdk_4.13.3-1_amd64.deb > /tmp/chefdk.deb && \
    dpkg -i /tmp/chefdk.deb && \
    rm /tmp/chefdk.deb

# Install Packer
RUN curl -L https://releases.hashicorp.com/packer/1.7.8/packer_1.7.8_linux_amd64.zip > /tmp/packer.zip && \
    unzip /tmp/packer.zip -d /usr/local/bin && \
    rm /tmp/packer.zip

# Create a symlink for nodejs to be accessible as 'node' if it does not exist
RUN if [ ! -f /usr/bin/node ]; then ln -s /usr/bin/nodejs /usr/bin/node; fi && \
    npm install -g azure-cli

# Ensure the /etc/sudoers.d/ directory exists and configure sudoers for Jenkins user
RUN mkdir -p /etc/sudoers.d/ && \
    echo "jenkins ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/jenkins

# Install Docker using the convenient script
RUN curl -sSL https://get.docker.com/ | sh

# Add wrapdocker to the image
ADD wrapdocker /usr/local/bin/wrapdocker
RUN chmod +x /usr/local/bin/wrapdocker

# Add user Jenkins to the docker group
RUN usermod -a -G docker jenkins

# Add the Jenkins slave startup script and set it as the entrypoint
ADD jenkins-slave-startup.sh /jenkins-slave-startup.sh
RUN chmod +x /jenkins-slave-startup.sh

# Switch back to the jenkins user
USER jenkins

# Define a volume for Jenkins home directory
VOLUME /home/jenkins

# Expose the standard SSH port
EXPOSE 22

ENTRYPOINT ["/jenkins-slave-startup.sh"]
