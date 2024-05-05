# Use an official Jenkins agent image as a parent image
FROM evarga/jenkins-slave

# Set the maintainer label
LABEL maintainer="Ameya Agashe <ameya.agashe@karbonhq.com>"

# Set environment variables
ENV DEBIAN_FRONTEND=noninteractive

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
    zlib1g-dev \
    libssl-dev \
    libxml2-dev \
    git-core \
    git-flow \
    python-pip \
    unzip

# Clean up to reduce layer size
RUN apt-get clean && rm -rf /var/lib/apt/lists/*

# Install Git LFS
RUN curl -s https://packagecloud.io/install/repositories/github/git-lfs/script.deb.sh | bash && \
    apt-get update && \
    apt-get install -y git-lfs && \
    git lfs install

# Install ChefDK
RUN curl -L "https://opscode-omnibus-packages.s3.amazonaws.com/debian/6/x86_64/chefdk_0.10.0-1_amd64.deb" -o /tmp/chefdk.deb && \
    dpkg -i /tmp/chefdk.deb && \
    rm /tmp/chefdk.deb

# Install Packer
RUN curl -L "https://releases.hashicorp.com/packer/0.10.1/packer_0.10.1_linux_amd64.zip" -o /tmp/packer.zip && \
    unzip /tmp/packer.zip -d /usr/local/bin && \
    rm /tmp/packer.zip

# Install Azure CLI
RUN npm install -g azure-cli

# Install PowerShell
RUN apt-get update && apt-get install -y wget apt-transport-https software-properties-common \
    && wget -q "https://packages.microsoft.com/config/debian/10/packages-microsoft-prod.deb" \
    && dpkg -i packages-microsoft-prod.deb \
    && apt-get update && apt-get install -y powershell \
    && rm packages-microsoft-prod.deb && apt-get clean && rm -rf /var/lib/apt/lists/*

# Configuring sudoers for Jenkins user
RUN echo "jenkins ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/jenkins

# Install Docker using the convenient script
RUN curl -sSL https://get.docker.com/ | sh

# Docker wrapper and permissions
ADD wrapdocker /usr/local/bin/wrapdocker
RUN chmod +x /usr/local/bin/wrapdocker && \
    usermod -a -G docker jenkins

# Setup Jenkins user environment
USER jenkins
RUN gpg --keyserver hkp://keys.gnupg.net --recv-keys 409B6B1796C275462A1703113804BB82D39DC0E3
RUN curl -sSL https://get.rvm.io | bash -s stable --ruby

# Switch back to root user
USER root

# Jenkins home directory and SSH port
VOLUME /home/jenkins
EXPOSE 22

# Jenkins slave startup script
ADD jenkins-slave-startup.sh /jenkins-slave-startup.sh
RUN chmod +x /jenkins-slave-startup.sh
ENTRYPOINT ["/jenkins-slave-startup.sh"]
