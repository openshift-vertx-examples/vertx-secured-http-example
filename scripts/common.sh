REALM=master
USER=admin
PASSWORD=admin
CLIENT_ID=demoapp
SECRET=cb7a8528-ad53-4b2e-afb8-72e9795c27c8
SSO_HOST=${1:-https://secure-sso-sso.e8ca.engint.openshiftapps.com}
APP=${2:-http://secured-vertx-rest-sso.e8ca.engint.openshiftapps.com}
# CLIENT_ID=vertx
# SECRET=ffdf9fec-aff3-4e22-bde1-8168aa9e24f6
# SSO_HOST=${1:-http://localhost:8180}
# APP=${2:-http://localhost:8080}

function jsonValue() {
  KEY=$1
  num=$2
  awk -F"[,:}]" '{for(i=1;i<=NF;i++){if($i~/'$KEY'\042/){print $(i+1)}}}' | tr -d '"' | sed -n ${num}p
}
