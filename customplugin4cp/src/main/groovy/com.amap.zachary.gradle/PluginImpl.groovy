package com.amap.zachary.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import com.amap.zachary.gradle.extension.PropertySettingExtension
import com.amap.zachary.gradle.task.PropertySettingTask
import com.amap.zachary.gradle.task.PropertyResettingTask
public class PluginImpl implements Plugin<Project>{
    final static PLUGIN_NAME = "custom_setting"
    final static TASK_NAME = "resolveCustomSetting"
    final static RETASK_NAME = "resolveCustomReSetting"
    final static TASK_GROUP = "Android"
    final static TASK_DESCRIPTION = "Resolve file field setting"
    final static RETASK_DESCRIPTION = "Resolve file field resetting"
    final static TASK_ATTACH_TO_LIFECYCLE = "preBuild"
    final static RETASK_ATTACH_TO_LIFECYCLE = "build"
    @Override
    void apply(Project project){
        project.configure(project) {
            extensions.create(PLUGIN_NAME, PropertySettingExtension)
        }
        project.afterEvaluate { evaluateResult ->
            if (null == evaluateResult.state.getFailure()) {
                Task task = project.task(TASK_NAME, type: PropertySettingTask)
                task.setDescription(TASK_DESCRIPTION)
                task.setGroup(TASK_GROUP)
                task.customsetting= project.custom_setting.customsetting
                task.customreplace= project.custom_setting.replaceorigin
                task.debug= project.custom_setting.debug
                if(project.custom_setting.debug)println "settingDependsonTask=" +project.custom_setting.settingDependsonTask+"&resettingDependsonTask"+project.custom_setting.resettingDependsonTask
                if(project.custom_setting.settingDependsonTask==null){
                    project.tasks.findByName(TASK_ATTACH_TO_LIFECYCLE).dependsOn task
                }else if(project.tasks.findByName(project.custom_setting.settingDependsonTask)!=null){
                    project.tasks.findByName(project.custom_setting.settingDependsonTask).dependsOn task
                }


                Task retask = project.task(RETASK_NAME, type: PropertyResettingTask)
                retask.setDescription(RETASK_DESCRIPTION)
                retask.setGroup(TASK_GROUP)
                retask.customsetting= project.custom_setting.customsetting
                retask.replaceorigin= project.custom_setting.replaceorigin
                retask.debug= project.custom_setting.debug
                if(project.custom_setting.resettingDependsonTask==null){
                    project.tasks.findByName(RETASK_ATTACH_TO_LIFECYCLE).dependsOn retask
                }else if(project.tasks.findByName(project.custom_setting.resettingDependsonTask)!=null){
                    project.tasks.findByName(project.custom_setting.resettingDependsonTask).dependsOn retask
                }
            }
        }
    }

}