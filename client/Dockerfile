### STAGE 1: Build ###
FROM node:12.7-alpine AS build
WORKDIR /usr/src/app
COPY ./ /usr/src/app/
RUN npm install
RUN node_modules/.bin/ng build --configuration=docker

### STAGE 2: Run ###
FROM nginx:1.17.1-alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /usr/src/app/dist/client /usr/share/nginx/html
EXPOSE 4200
