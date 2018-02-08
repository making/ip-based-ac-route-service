

```
curl localhost:8080/foo \
   -H "X-CF-Forwarded-Url: http://httpbin.org/get" \
   -H "X-CF-Proxy-Metadata: a" \
   -H "X-CF-Proxy-Signature: a" \
   -H "X-Forwarded-For: 192.168.10.1"
```


### Cloud Foundry


```
./mvnw clean package
cf push
```

Create a route service

```
export APP_DOMAIN=cfapps.io
export TARGET=foo
cf create-user-provided-service ip-based-ac-${TARGET} -r https://ip-based-ac-route-service.${APP_DOMAIN}/${TARGET}
```

Bind the route service above

```
cf bind-route-service ${APP_DOMAIN} ip-based-ac-${TARGET} --hostname your-app
```

Demo

```
$ curl your-app.${APP_DOMAIN}
{"message":"192.169.54.50 is not allowed to access foo."}

$ curl -XPUT -H "Content-Type: application/json" ip-based-ac-route-service.${APP_DOMAIN}/acl/${TARGET} -d '["192.168.10.0/24","192.168.20.0/24", "192.169.54.50/32"]' 
["192.168.10.0/24","192.168.20.0/24","192.169.54.50/32"]

$ curl demo.202-241-169-198.sslip.io 
Hello World! 
```
