package com.amap.zachary.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs


class PropertyResettingTask extends DefaultTask{

    final Logger log = Logging.getLogger PropertyResettingTask
    final  String CONFIGURATION_SEPARATOR = ":"
    final String TMP_DIR = "zachary_tmp"
    final String ORIGIN_DIR = "origin"

    File tmp
    File tmp_origin
    def debug = false
    def customsetting=[]
    def replaceorigin=[]
    PropertyResettingTask(){
        getOutputs().upToDateWhen {return false}//always rerun
    }
    @TaskAction
    def exec(IncrementalTaskInputs inputs){

        log.lifecycle "Executing PropertyReSettingTask"
        tmp = new File(project.getProjectDir(),TMP_DIR)
        tmp_origin = new File(tmp,ORIGIN_DIR)
        customsetting.each{ setting->
            if(debug)println "Processing setting: '$setting'"
            log.info "Processing setting: '$setting'"
            resetFile setting
        }

        replaceorigin.each{ replace->
            if(debug)println "Processing replace: '$replace'"
            log.info "Processing replace: '$replace'"
            resetFile replace
        }
        if(!debug){
            deleteFile tmp
        }
    }
    def resetFile(String setting){
        log.info "setValue2File setting: '$setting'"

        def setstring = setting.tokenize(CONFIGURATION_SEPARATOR)
        def classname = setstring[0]
        if(!classname.contains("src/main/java/")){
            classname = project.getProjectDir().toString()+"/src/main/java/"+classname
        }else{
            classname = project.getProjectDir().toString()+classname
        }

        if(debug)println "classname: '$classname'"
        log.info "classname: '$classname'"
        try{
            def tofile = new File(tmp,classname.substring(classname.indexOf(project.name)+project.name.length()))
            def origin = new File(tmp_origin,classname.substring(classname.indexOf(project.name)+project.name.length()))
            if(debug)println "tofile"+tofile.toString()

            def f = new File(classname)
            if(f.exists()){
//                f.eachLine{line ->
//                    if(line.contains(" s ")){
//                        log.info "line: '$line'"
//                         println "line: '$line'"
//                    }
//                }
                if(origin.exists()){
                    PropertySettingTask.copyFile(origin,f)//copy origin file back
                    origin.delete()
                }

            }
        }catch(Exception e){
            e.printStackTrace()

        }
    }
    def static deleteFile(File f){
        if(f!=null&&f.exists()){
            if(f.isDirectory()){
                for(File ff:f.listFiles()){
                    deleteFile(ff)
                }
                f.delete()
            }else if(f.isFile()){
                f.delete()
            }
        }
    }

}