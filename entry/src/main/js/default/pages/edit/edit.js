import router from '@system.router';
// abilityType: 0-Ability; 1-Internal Ability
const ABILITY_TYPE_EXTERNAL = 0;
const ABILITY_TYPE_INTERNAL = 1;
// syncOption(Optional, default sync): 0-Sync; 1-Async
const ACTION_SYNC = 0;
const ACTION_ASYNC = 1;
const ACTION_MESSAGE_SELECT_TODOS = 1001;
const ACTION_MESSAGE_DELETE_TODOS = 1002;
const ACTION_MESSAGE_UPDATE_TODOS = 1003;
const ACTION_MESSAGE_INSERT_TODOS = 1004;
const ACTION_MESSAGE_SELECT_TODO = 1005;


export default {
    data:{
        title: "",
        text: "",
        content: {},
    },
    onShow(){
        console.info("edit.onShow()");
        if (this.content == null) {
            this.title = "";
        }
        else {
            this.title = this.content.title;
        };
        this.getText();
    },
    ClickToBack() {
        console.info("ClickToBack()");
        router.back();
    },
    ClickToSave(){
        console.info("ClickToSave()");
        this.insertTodos();
        // 需要和数据库交互
    },
    passTitle(passtitle){
        this.title = passtitle.text;
//        console.info(this.title);

    },
    passText(passtext){
        this.text = passtext.text;
//        console.info(this.text);
    },
    insertTodos: async function(){
        var actionData = {};
        actionData.id = this.index;
        actionData.title = this.title;
        actionData.text = this.text;

        var d = new Date();//获取系统当前时间
        actionData.date = d.getFullYear()+"/"+d.getMonth()+"/"+d.getDate();   // TODO

        var action = {};
        action.bundleName = 'com.example.backup';
        action.abilityName = 'com.example.backup.TodoServiceAbility';
        action.messageCode = ACTION_MESSAGE_INSERT_TODOS;
        action.data = actionData;
        action.abilityType = ABILITY_TYPE_INTERNAL;
        action.syncOption = ACTION_SYNC;

        var result = await FeatureAbility.callAbility(action);
        console.info("insert ret="+result);
    },
    getText: async function(){
        console.log("get Test of "+this.index);
        var actionData = {};
        actionData.id = this.index;

        var action = {};
        action.bundleName = 'com.example.backup';
        action.abilityName = 'com.example.backup.TodoServiceAbility';
        action.messageCode = ACTION_MESSAGE_SELECT_TODO;
        action.data = actionData;
        action.abilityType = ABILITY_TYPE_INTERNAL;
        action.syncOption = ACTION_SYNC;

        var result = await FeatureAbility.callAbility(action);
        this.text = result;
    },
    // 页面迁移 ===========================================================
    onRestoreData(restoreData) {
        // 收到迁移数据，恢复。
        this.continueAbilityData = restoreData;
    },
    tryContinueAbility: async function() {
        // 应用进行迁移
        let result = await FeatureAbility.continueAbility();
        console.info("result:" + JSON.stringify(result));
    },
    onStartContinuation() {
        // 判断当前的状态是不是适合迁移
        console.info("onStartContinuation");
        return true;
    },
    onCompleteContinuation(code) {
        // 迁移操作完成，code返回结果
        console.info("nCompleteContinuation: code = " + code);
    },
    onSaveData(saveData) {
        // 数据保存到savedData中进行迁移。
        var data = this.shareData;
        Object.assign(saveData, data);
        //        console.info(JSON.stringfy(saveData));
    }
}