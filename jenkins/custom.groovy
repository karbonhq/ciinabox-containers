import jenkins.*
import hudson.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import hudson.plugins.sshslaves.*;
import hudson.model.*
import jenkins.model.*
import hudson.security.*
import hudson.slaves.*
import hudson.plugins.sshslaves.*

def instance = Jenkins.getInstance()
instance.setNumExecutors(0)

user = hudson.model.User.get('ciinabox',false)

if(user == null) {
  user = hudson.model.User.get('ciinabox')
  user.setFullName('ciinabox')
  email = new hudson.tasks.Mailer.UserProperty('ciinabox@base2services.com')
  user.addProperty(email)
  password = hudson.security.HudsonPrivateSecurityRealm.Details.fromPlainPassword('ciinabox')
  user.addProperty(password)
  user.save()

  def realm = new HudsonPrivateSecurityRealm(false)
  instance.setSecurityRealm(realm)
  def strategy = new hudson.security.ProjectMatrixAuthorizationStrategy()
  strategy.add(Jenkins.ADMINISTER, "ciinabox")
  instance.setAuthorizationStrategy(strategy)
  instance.save()
} else {
    println("ciinabox user and default security already setup")
}


def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
      com.cloudbees.plugins.credentials.common.StandardUsernameCredentials.class,
      Jenkins.instance,
      null,
      null
);

def jenkinsCreds = null
for (c in creds) {
  if(c.username == 'jenkins') {
    addJenkinsCred = c
    break
  }
}

if(jenkinsCreds == null) {
  global_domain = Domain.global()
  credentials_store =
  Jenkins.instance.getExtensionList(
  'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
  )[0].getStore()
  jenkinsCreds = new UsernamePasswordCredentialsImpl(
  CredentialsScope.GLOBAL,
  null,
  "jenkins",
  "jenkins",
  "jenkins")
  credentials_store.addCredentials(global_domain, jenkinsCreds)
} else {
 println("jenkins creds already exists")
}

Jenkins.instance.addNode(new DumbSlave("jenkins-docker-slave","Jenkins Docker Slave","/home/jenkins","8",Node.Mode.NORMAL,"docker",
  new SSHLauncher("172.17.0.1",2223,jenkinsCreds,null,null,null,null,null,null,null,null),new RetentionStrategy.Always(),new LinkedList()))
