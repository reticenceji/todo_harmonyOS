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
            this.text = "";
        }
        else {
            this.title = this.content.title;
            this.text = this.content.text;
        };
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
        actionData.date = "2021";   // TODO

        var action = {};
        action.bundleName = 'com.example.backup';
        action.abilityName = 'com.example.backup.TodoServiceAbility';
        action.messageCode = ACTION_MESSAGE_INSERT_TODOS;
        action.data = actionData;
        action.abilityType = ABILITY_TYPE_INTERNAL;
        action.syncOption = ACTION_SYNC;

        var result = await FeatureAbility.callAbility(action);
        console.info("insert ret="+result);
//        var ret = JSON.parse(result);

    }
}