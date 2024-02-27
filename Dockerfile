FROM gradle:jdk17-jammy as build

ENV DEBIAN_FRONTEND=noninteractive

COPY ./ /build

WORKDIR /build

RUN gradle server:dist

FROM openjdk:17-jdk-slim

WORKDIR app

ARG port=25533
ARG savename=default
ARG seed=0
ENV PORT=$port
ENV SAVENAME=$savename
ENV SEED=$seed

EXPOSE ${PORT}

COPY --from=build /build/server/build/libs/server-1.0.jar .

CMD java -jar server-1.0.jar -p $PORT -s $SAVENAME --seed $SEED
