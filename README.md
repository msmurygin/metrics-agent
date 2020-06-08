# metrics-agent
Для запуска агента,For agent usage add the folowing  property
metrics.registry.filename - config file
jboss.deployment.home application deployment home
jboss.deployment.ear= application ear/war/jar name
patch.lib.file.jar = if you classes in a sub-jar file, profider the lib jar name
patch.lib.path = sub jar location
temp.file.path = OS temp dir
javaagent= path to agent.


set AGENT_PATH=-Dmetrics.registry.filename=C:\Infor\sce\scprd\wm\app\conf\settings.json \
-Djboss.deployment.home=%JBOSS_HOME%\wmapp1\deployments\ \
-Djboss.deployment.ear=wmserver.ear \
-Dpatch.lib.file.jar=app-server-9.2.4-.jar 
-Dpatch.lib.path=lib/base/ 
-Dtemp.file.path=C:\tmp\ \
-javaagent:C:\Infor\sce\jboss-as-7.2.0.Final\bin\agentlogger-jar-with-dependencies.jar

settings.json example
{
"settings" : 
[
{
	"PackageName" : "com.ssaglobal.scm.wms.service.drfmanagement",
	"className" : "*",
	"Methods" : ["*"]
}
]
}
