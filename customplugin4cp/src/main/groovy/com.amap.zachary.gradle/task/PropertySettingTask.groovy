package com.amap.zachary.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

import java.util.regex.Matcher
import java.util.regex.Pattern

class PropertySettingTask extends DefaultTask{
    def customsetting
    def customreplace
    final Logger log = Logging.getLogger PropertySettingTask
    final  String CONFIGURATION_SEPARATOR = ":"
    final String TMP_DIR = "zachary_tmp"
    final String ORIGIN_DIR = "origin"
    def debug = false
    File tmp
    File tmp_origin
    PropertySettingTask(){
        getOutputs().upToDateWhen {return false}//always rerun
    }
    @TaskAction
    def exec(IncrementalTaskInputs inputs){

        log.lifecycle "Executing PropertySettingTask"

//        println "lasttask = "+project.tasks.last().name
//        println "if exsits resolveCustomReSetting is "+(project.tasks.findByName("resolveCustomReSetting")!=null)

        tmp = new File(project.getProjectDir(),TMP_DIR)
        tmp_origin = new File(tmp,ORIGIN_DIR)
        if(!tmp.exists())tmp.mkdirs()
        customsetting.each{ setting->
            if(debug)println "Processing setting: '$setting'"
            log.info "Processing setting: '$setting'"
            setValue2File setting
        }
        customreplace.each{ replace->
            if(debug)println "Processing replace: '$replace'"
            replaceValue replace
        }
    }
    def setValue2File(String setting){
        log.info "setValue2File setting: '$setting'"

        if(debug)println "setValue2File setting: '$setting'"
        def setstring = setting.tokenize(CONFIGURATION_SEPARATOR)
        def classname = setstring[0]
        def field = setstring[1]
        def value = setstring[2]
        if(!classname.contains("/src/main/java/")&&classname.endsWith(".java")){
            classname = project.getProjectDir().toString()+"/src/main/java/"+classname
        }else{
            classname = project.getProjectDir().toString()+"/"+classname
        }

        if(debug)println "classname: '$classname',field: '$field',value: '$value'"+"  pro:"+project.name
        log.info "classname: '$classname',field: '$field',value: '$value'"
        try{
            def tofile = new File(tmp,classname.substring(classname.indexOf(project.name)+project.name.length()))
            def origin = new File(tmp_origin,classname.substring(classname.indexOf(project.name)+project.name.length()))
            println "tofile"+tofile.toString()

            def f = new File(classname)
            def p
            if(classname.endsWith(".java")){
                p= Pattern.compile("\\w+\\s+"+field+"\\s*=(.*);")
            }else{
                p= Pattern.compile(field+"\\s*=(.*)")
            }

            if(f.exists()){
//                f.eachLine{line ->
//                    if(line.contains(" s ")){
//                        log.info "line: '$line'"
//                         println "line: '$line'"
//                    }
//                }
                if(!origin.exists()){
                    createFile(origin)
                    copyFile(f,origin)
                }
                if(!tofile.exists()){
                    createFile(tofile)
                }
                copyFile(f,tofile)//copy to tmp dir
                def closure = { line ->
                    Matcher m = p.matcher(line);
                    if(m.find()){
                        if(line.contains(" String ")){
                            return line.replaceAll(m.group(1),"\""+value+"\"")
                        }else{
                            return line.replaceAll(m.group(1),value)
                        }

                    }else{
                        return line
                    }
                }
                copyFile(tofile,f,closure)//overwrite origin file with field set value
            }else{
                println "no such file: '$classname'"
                 log.info "no such file: '$classname'"
            }
        }catch(Exception e){
            e.printStackTrace()
            println "read file failed: '$classname'"
            log.info "read file failed: '$classname'"
        }
    }
    def replaceValue(String replace){
        log.info "replaceValue replace: '$replace'"

        if(debug)println "replaceValue replace: '$replace'"
        def replacestring = replace.tokenize(CONFIGURATION_SEPARATOR)
        def classname = replacestring[0]
        def reg = replacestring[1]
        def value = replacestring[2]
        if(!classname.contains("/src/main/java/")&&classname.endsWith(".java")){
            classname = project.getProjectDir().toString()+"/src/main/java/"+classname
        }else{
            classname = project.getProjectDir().toString()+"/"+classname
        }

        if(debug)println "classname: '$classname',field: '$reg',value: '$value'"+"  pro:"+project.name
        log.info "classname: '$classname',field: '$reg',value: '$value'"
        try{
            def tofile = new File(tmp,classname.substring(classname.indexOf(project.name)+project.name.length()))
            def origin = new File(tmp_origin,classname.substring(classname.indexOf(project.name)+project.name.length()))
            if(debug)println "tofile:"+tofile.toString()

            def f = new File(classname)
            def p = Pattern.compile(reg)
            if(f.exists()){
                if(!origin.exists()){
                    createFile(origin)
                    copyFile(f,origin)
                }
                if(!tofile.exists()){
                    createFile(tofile)
                }
                copyFile(f,tofile)//copy to tmp dir
                def closure = { line ->
                    Matcher m = p.matcher(line);
                    if(m.find()){
                        if(reg.contains("(")&&reg.contains(")")){
                            return line.replaceAll(m.group(1),value)
                        }else{
                            return line.replaceAll(m.group(0),value)
                        }

                    }else{
                        return line
                    }
                }
                copyFile(tofile,f,closure)//overwrite origin file with field set value
            }else{
                println "no such file: '$classname'"
                 log.info "no such file: '$classname'"
            }
        }catch(Exception e){
            e.printStackTrace()
            println "read file failed: '$classname'"
            log.info "read file failed: '$classname'"
        }
    }
    def static copyFile(File from,File to,Closure... closure){
        //println "copyFile"+closure.size()
        to.withWriter { file ->
            from.eachLine { line ->
                if(closure.size()>0){
                    file.writeLine(closure[0].call(line))
                }else{
                    file.writeLine(line)
                }
            }
        }
        return true;
    }
    def static createFile(File f){
        if(!f.exists()) {
            if(!f.getParentFile().exists()){
               f.getParentFile().mkdirs()
            }
            f.createNewFile()
        }
    }
}