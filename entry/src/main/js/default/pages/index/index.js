// @ts-nocheck
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
// shareData的数据会在onSaveData触发时与saveData一起传送到迁移目标FA，并绑定到其shareData数据段上
// shareData的数据可以直接使用this访问。eg:this.remoteShareData1
    data: {
        continueAbilityData: {},
    },
    shareData: {
        title: "",
        search: "",
    // 国际化字符串
        number: 0, // TODO总数
        add_delete: "+", // 添加按钮/删除按钮
        choose_delete: false, // 当为true的时候，选择删除元素
        choose_indexes: [], // 被选择要删除的TODO列表
        todoList: [], // TODO列表 index,title,date
        max_index: 0,
    },
// 初始化
    onInit() {
        this.title = this.$t('strings.title');
        this.search = this.$t('strings.search');
        this.number = this.todoList.length.toString() + this.$t('strings.number');
        console.info("onInit");
        console.info(this.textList);
    },
    onShow() {
        this.getTodos();
        this.number = this.todoList.length.toString() + this.$t('strings.number');
    },
// 长按TODO text 可以选择删除
    LongPressToDelete() {
        this.choose_delete = true;
    },
// 单击TODO text 可以编辑 页面跳转
    ClickToEdit(x) {
        console.info("ClickToEdit(" + x.toString() + ")");
        router.push({
            uri: 'pages/edit/edit', // 指定要跳转的页面
            params: {
                index: this.todoList[x].index,
                content: this.todoList[x]
            }
        });
    },
// 单击按钮 添加/删除
    addTODO() {
        if (this.choose_delete == false) {
            router.push({
                uri: 'pages/edit/edit', // 指定要跳转的页面 记得在config中注册路由
                params: {
                    index: this.max_index,
                    content: null
                }
            });
            this.max_index++;
        }
        else {
            // 删除
            this.deleteTodos();
            this.choose_indexes = [];
        }
        this.choose_delete = false;
        this.add_delete = "+";
    },
// 选择删除的TODO
    ClickToDelete(value, e) {
        console.info("ClickToDelete" + e.checked.toString());
        if (e.checked) {
            this.add_delete = "-";
            this.choose_indexes.push(value);
            console.info("indexes: " + this.choose_indexes.toString());
        }
        else {
            this.choose_indexes.splice(this.choose_indexes.indexOf(value), 1);
            console.info("indexes: " + this.choose_indexes.toString());
        }
        ;

        if (this.choose_indexes.length == 0) {
            this.add_delete = "+";
        }
    },
// 希望得到的返回数据是所有的title和date
    getTodos: async function(){
        console.info("try to get TODOs");
        var actionData = {};

        var action = {};
        action.bundleName = 'com.example.backup';
        action.abilityName = 'com.example.backup.TodoServiceAbility';
        action.messageCode = ACTION_MESSAGE_SELECT_TODOS;
        action.data = actionData;
        action.abilityType = ABILITY_TYPE_INTERNAL;
        action.syncOption = ACTION_SYNC;

        var result = await FeatureAbility.callAbility(action);
        console.info("getTodos" + result);

        var jj = JSON.parse(result);

        this.todoList = [];
        var n = 0;
        var k;
        for (var i in jj) {
            k = jj[i].replace(/\\/, "");
            this.todoList[n] = JSON.parse(k);
            this.todoList[n].index = i;
            n++;
        }
        ;
    },
    deleteTodos: async function(){
        console.info("deleteTodos");
        var actionData = {};
        var action = {};
        action.bundleName = 'com.example.backup';
        action.abilityName = 'com.example.backup.TodoServiceAbility';
        action.messageCode = ACTION_MESSAGE_DELETE_TODOS;
        action.abilityType = ABILITY_TYPE_INTERNAL;
        action.syncOption = ACTION_SYNC;
        for (var i in this.choose_indexes) {
            //            console.info(this.choose_indexes);
            //            console.info(JSON.stringify(this.todoList));
            //            console.info("Try to delete "+i+ JSON.stringify(this.todoList[this.choose_index[i]]));
            var ii = this.choose_indexes[i];
            actionData.id = this.todoList[ii].index;
            action.data = actionData;

            var result = await FeatureAbility.callAbility(action);
            //            this.todoList = JSON.parse(result);
        }
        this.getTodos();
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
    },
}
