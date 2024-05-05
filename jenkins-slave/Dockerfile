# Use an official Jenkins agent image as a parent image
FROM jenkins/agent:latest

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
    openssh-server

# Configure SSH daemon with a dedicated user (more secure)
RUN mkdir -p /etc/sudoers.d \
&& echo "jenskins ALL=(ALL) NOPASSWD: /usr/local/bin/jenkins-agent" > /etc/sudoers.d/jenkins \
&& rm -f /etc/ssh/ssh_host_*_key /etc/ssh/ssh_host_*_key.pub \
&& ssh-keygen -t rsa -f /etc/ssh/ssh_host_rsa_key -N '' \
&& ssh-keygen -t dsa -f /etc/ssh/ssh_host_dsa_key -N '' \
&& ssh-keygen -t ecdsa -f /etc/ssh/ssh_host_ecdsa_key -N '' \
&& ssh-keygen -t ed25519 -f /etc/ssh/ssh_host_ed25519_key -N '' \
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

# Install additional tools
RUN curl -L https://packages.chef.io/files/stable/chefdk/4.13.3/debian/10/chefdk_4.13.3-1_amd64.deb > /tmp/chefdk.deb && \
    dpkg -i /tmp/chefdk.deb && \
    rm /tmp/chefdk.deb

RUN curl -L https://releases.hashicorp.com/packer/1.7.8/packer_1.7.8_linux_amd64.zip > /tmp/packer.zip && \
    unzip /tmp/packer.zip -d /usr/local/bin && \
    rm /tmp/packer.zip

# Node.js and Azure CLI
RUN if [ ! -f /usr/bin/node ]; then ln -s /usr/bin/nodejs /usr/bin/node; fi && \
    npm install -g azure-cli

# Docker installation
RUN curl -sSL https://get.docker.com/ | sh

# Docker permissions
ADD wrapdocker /usr/local/bin/wrapdocker
RUN chmod +x /usr/local/bin/wrapdocker && \
    usermod -a -G docker jenkins

# Switch to Jenkins user
USER jenkins

# Set volume for Jenkins home
VOLUME /home/jenkins

# Expose SSH port
EXPOSE 22

# Start SSH daemon
CMD ["/usr/sbin/sshd", "-D"]
