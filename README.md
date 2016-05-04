# Demo of A-MQ on OpenShift v3
## Prerequisites
- OpenShift v3 instance
- A-MQ image stream in openshift namespace named jboss-amq-62
- this repositry

## Preparation
Start with creating a new project so you have clean namespace to work with.

Create a template (in your project) from this repository: ```oc create -f template```. This will allow you to create the application
from this template any time in the future.

To be able to connect to A-MQ from outside OpenShift, you need to set-up an SSL. For this you need broker keystore and
client keystore and truststore. You can follow the steps from [activemq documentation](http://activemq.apache.org/how-do-i-use-ssl.html).
Once you have broker keystore, create a service account and secret with this service account. Service account name is hardcoded in the template.
```
oc create serviceaccount amq-service-account
oc secret new amq-app-secret broker.ks
oc secret add serviceaccount/amq-service-account secret/amq-app-secret
```

To allow clustering (using Mesh topology), the service account have to have 'view' permissions to the project. By default, it does not
have it, so add them using command
```
oc policy add-role-to-user view system:serviceaccount:<your_namespace>:amq-service-account
```

## Creating application
