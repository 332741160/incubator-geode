#!/bin/bash
# Set GEMFIRE to the product toplevel directory
GEMFIRE=`dirname $0`
OLDPWD=$PWD
cd $GEMFIRE
GEMFIRE=`dirname $PWD`
cd $OLDPWD

if [ "x$WINDIR" != "x" ]; then
  echo "ERROR: The variable WINDIR is set indicating this script is running in a Windows OS, please use the .bat file version instead."
  exit 1
fi

GEMFIRE_JARS=$GEMFIRE/lib/gemfire.jar

if [ ! -f "${GEMFIRE_JARS}" ]; then
  echo "ERROR: Could not determine GEMFIRE location."
  exit 1
fi

LOG4J_API=$( ls $GEMFIRE/lib/log4j-api*jar )
LOG4J_CORE=$( ls $GEMFIRE/lib/log4j-core*jar )

GEMFIRE_JARS=$GEMFIRE_JARS:$GEMFIRE/lib/antlr.jar:$LOG4J_API:$LOG4J_CORE

# Initialize classpath
GEMFIRE_JARS=$GEMFIRE_JARS:$GEMFIRE/lib/gemfire-modules-@GEMFIRE_MODULES_VERSION@.jar \
GEMFIRE_JARS=$GEMFIRE_JARS:$GEMFIRE/lib/gemfire-modules-session-@GEMFIRE_MODULES_VERSION@.jar \
GEMFIRE_JARS=$GEMFIRE_JARS:$GEMFIRE/lib/servlet-api-@SERVLET_API_VERSION@.jar \
GEMFIRE_JARS=$GEMFIRE_JARS:$GEMFIRE/lib/gemfire-modules-session-external-@GEMFIRE_MODULES_VERSION@.jar \
GEMFIRE_JARS=$GEMFIRE_JARS:$GEMFIRE/lib/slf4j-api-@SLF4J_VERSION@.jar \
GEMFIRE_JARS=$GEMFIRE_JARS:$GEMFIRE/lib/slf4j-jdk14-@SLF4J_VERSION@.jar

# Add configuration
GEMFIRE_JARS=$GEMFIRE_JARS:$GEMFIRE/conf

if [ "x$CLASSPATH" != "x" ]; then
  GEMFIRE_JARS=$GEMFIRE_JARS:$CLASSPATH
fi

${GF_JAVA:-java} ${JAVA_ARGS} -classpath ${GEMFIRE_JARS} com.gemstone.gemfire.internal.cache.CacheServerLauncher "$@"
