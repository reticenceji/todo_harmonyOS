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
        BACK: "",
        SAVE: "",
        CANCEL: "",
        CONFIRM: "",

        ddl_text: "",
        ddls: [1,2,3],
        ddl_to_pass: "",
        ddl: "",
        ddl_exist: false,
        content: {},

    },
    onInit() {
        this.ddl_text = this.$t('strings.ddl');
        this.ddl1_text = this.$t('strings.ddl1');
        this.ddl2_text = this.$t('strings.ddl2');
        this.ddl3_text = this.$t('strings.ddl3');
        this.BACK = this.$t('strings.back');
        this.CANCEL = this.$t('strings.cancel');
        this.CONFIRM = this.$t('strings.confirm');
        this.SAVE = this.$t('strings.save');
    },
    onShow(){
        console.info("edit.onShow()");
        this.title = this.content.title;
        this.date = this.content.date;
        if (this.content.hasOwnProperty("ddl")) {
            this.ddl_exist = true;
            this.ddls = [50,75,90];
            this.ddl = this.content.ddl;
        }
        else {
            this.ddl_exist = false;
            this.ddl = "2050/10/1";
            this.ddls = [50,75,90];
        }
        this.GetText();
        console.info("ddl"+this.ddl);
    },
    PassDdl(e){
        this.ddl = e.year+"/"+(e.month+1)+"/"+e.day;
    },
    PassDdls(id,e){
        this.ddls[id] = e.value;
        console.info("ddls["+id+"]="+this.ddls[id]);
    },
    PassTitle(pass_title){
        this.title = pass_title.text;
    },
    PassText(pass_text){
        this.text = pass_text.text;
    },
    ClickToBack() {
        console.info("ClickToBack()");
        router.back();
    },
    AddDdl(){
        this.$element('ddl_dialog').show();
    },
    GetToday(){
        var d = new Date();//获取系统当前时间
        var date = d.getFullYear()+"/"+(d.getMonth()+1)+"/"+d.getDate();
        return date;
    },
    SaveDdl(){
        var today = new Date();
        this.ddl_to_pass = this.ddl;
        for (var i in this.ddls){
            var ms = (Date.parse(this.ddl)-today.getTime())*this.ddls[i]/100+today.getTime();    // 距今的毫秒数
            var d = new Date();
            d.setTime(ms);
            var str = ","+d.getFullYear()+"/"+(d.getMonth()+1)+"/"+d.getDate();
            this.ddl_to_pass += str;
        }
        console.info("ddl_to_pass"+this.ddl_to_pass);
        this.ddl_exist = true;
        this.$element('ddl_dialog').close();
    },
    CancelDdl(){
        this.ddl_exist = false;
        this.$element('ddl_dialog').close();

    },
    InsertTodos: async function(){
        console.info("ClickToSave()");
        var actionData = {};
        actionData.id = this.index;
        actionData.title = this.title;
        actionData.text = this.text;
        actionData.date = this.GetToday();
        if (this.ddl_exist)
        {actionData.ddl = this.ddl_to_pass;}

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
    GetText: async function(){
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
}
