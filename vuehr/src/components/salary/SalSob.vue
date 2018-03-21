<template>
  <div>
    <el-container>
      <el-header style="display: flex;justify-content: flex-start;align-items: center;padding-left: 0px">
        <el-button @click="dialogVisible = true;" icon="el-icon-plus" type="primary" size="mini">添加账套</el-button>
        <el-button @click="salaryItemDialogVisible = true;" icon="el-icon-plus" type="primary" size="mini">添加工资项目</el-button>
        <el-button size="mini" type="success" @click="loadSalaryAccountModeCfg" icon="el-icon-refresh"></el-button>
      </el-header>
      <el-main style="padding-left: 0px;padding-top: 0px">
        <div>
          <el-table
            :data="salaryAccountModeConfig"
            stripe
            v-loading="tableLoading"
            size="mini"
            @selection-change="handleSelectionChange"
            style="width: 100%">
            <el-table-column
              type="selection"
              width="30">
            </el-table-column>
            <el-table-column
              align="center"
              prop="modeName"
              label="账套名称">
            </el-table-column>

            <el-table-column v-for="item in salaryItem" :key="item.id"
                             :prop.number="item.id + '.amt'"
                             :label="item.itemName">
              <!--<template slot-scope="scope">-->
                <!--<label >{{ccc.amt}}</label>-->
              <!--</template>-->
            </el-table-column>
            <el-table-column label="操作" align="center">
              <el-table-column label="编辑" align="center">
                <template slot-scope="scope">
                  <el-button
                    size="mini"
                    @click="handleEdit(scope.$index, scope.row)">编辑
                  </el-button>
                </template>
              </el-table-column>
              <el-table-column label="删除" align="center">
                <template slot-scope="scope">
                  <el-button
                    size="mini"
                    type="danger"
                    @click="handleDelete(scope.$index, scope.row)">删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table-column>
          </el-table>
        </div>
        <div style="text-align: left;margin-top: 10px" v-if="salaries!=0">
          <el-button type="danger" round size="mini" :disabled="multipleSelection.length==0" @click="deleteAll">批量删除
          </el-button>
        </div>
      </el-main>
    </el-container>


    <div style="text-align: left">
      <el-dialog
        title="添加工资账套"
        :visible.sync="dialogVisible"
        width="50%"
        @close="emptySalary"
        @open="loadSalaryItems"
        :close-on-click-modal="false">
        <div style="display: flex;justify-content: flex-start;">
          <el-steps  :active="index" direction="vertical" space="30px" >
            <el-step v-for="item in salaryItem" :title="item.itemName" :key="item.id" size="mini" ></el-step>
          </el-steps>
          <div style="margin-left: 0px;display: flex;justify-content: center;align-items: center;width: 80%;">

            <div v-for="(item, key) in salaryItem" :key="item.id" v-show="index==key">
              <div style="margin-bottom: 15px;"><label style="color: red; font-size: medium">收支方向： {{item.itemType == 1 ? "收入" : "扣款"}}</label></div>

              {{item.itemName}}：
              <el-input
                :placeholder="'请输入' + item.itemName + '...'"
                size="mini"
                style="width: 200px"
                type="number"
                @keyup.enter.native="next"
                v-model="salaryAccountMode.items[item.id]"
              >
              </el-input>
            </div>
          </div>
        </div>
        <div style="display: flex;align-items: center;justify-content: center;padding: 0px;margin: 0px;">
          <el-button round size="mini" v-if="index!=0" @click="index--">上一步</el-button>
          <el-button type="primary" round size="mini" @click="next" v-text="index==salaryItem.length?'完成':'下一步'"></el-button>
        </div>
      </el-dialog>
    </div>

    <div style="text-align: left">
      <el-dialog
        title="添加工资项目"
        :visible.sync="salaryItemDialogVisible"
        width="50%"
        @close="emptySalary"
        @open="loadSalaryItems"
        :close-on-click-modal="false">
        <div style="display: flex;justify-content: flex-start;">

          <div style="margin-left: 0px;width: 80%;">
            <div>
              <label>项目名称：</label>
              <el-input
                :placeholder="'请输入工资项目的名称...'"
                size="mini"
                style="width: 200px"
                type="text"
                v-model="addSalaryItem.itemName"
                value="addSalaryItem.itemName"
              />
            </div>
            <div style="margin: 20px 0px 20px 0px;">
              <label>收支方向：</label>
              <el-radio-group v-model="addSalaryItem.itemType" size="medium">
                <el-radio-button label="收入"></el-radio-button>
                <el-radio-button label="扣款"></el-radio-button>
              </el-radio-group>
            </div>
            <div style="margin: 20px 0px 20px 0px;">
              <label>是否为固定配置：</label>
              <el-radio-group v-model="addSalaryItem.isFixedValue" size="medium">
                <el-radio-button label="是"></el-radio-button>
                <el-radio-button label="否"></el-radio-button>
              </el-radio-group>
            </div>
          </div>
        </div>
        <div style="display: flex;align-items: center;justify-content: center;padding: 0px;margin: 0px;">
          <el-button type="primary" round size="mini" @click="cancleAddSalaryItem" v-text="'取 消'"></el-button>
          <el-button type="primary" round size="mini" @click="saveSalaryItem" v-text="'添 加'"></el-button>
        </div>
      </el-dialog>
    </div>

  </div>
</template>
<script>
  export default {
    data() {
      return {
        dialogVisible: false,
        salaryItemDialogVisible : false,
        tableLoading: false,
        index: 0,
        ccc : {
            amt : '',
        },
        salaries: [],
        multipleSelection: [],
        salaryItem : [],
        salaryAccountMode : {
          items : {},
          salaryItems : {}
        },
        salaryAccountModeConfig : [],
        addSalaryItem : {
            itemName : '',
            itemType : '收入',
            isFixedValue : '是',
        }
      };
    },
    methods: {
      loadSalaryItems(){
        var _this = this;
        this.getRequest("/salary/item/findAllSalaryItems").then(resp=> {
          if (resp && resp.status == 200) {
            _this.salaryItem = resp.data;
          }
        })
      },
      deleteAll(){
        this.$confirm('删除[' + this.multipleSelection.length + ']条记录, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          var ids = '';
          this.multipleSelection.forEach(row=> {
            ids = ids + row.id + ",";
          })
          this.doDelete(ids);
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      handleEdit(index, row) {
        this.dialogVisible = true;
        this.salaryAccountMode.id = row['id'];
        this.salaryAccountMode.modeName = row["modeName"];
        for (let key in row) {
//          if (key.startsWith("item_id_")){
//            this.salaryAccountMode.items[row[key].id] = row[key].amt;
//          }
          this.salaryAccountMode.items[row[key].id] = row[key].amt;
        }

        row.createDate = new Date(row.createDate);
      },
      handleDelete(index, row) {
        this.$confirm('删除【' + row.modeName + '】账套, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.doDelete(row.id);
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },
      doDelete(id){
        var _this = this;
        _this.tableLoading = true;
        this.deleteRequest("/salary/accountMode/delSalaryAccountMode?id=" + id).then(resp=>{
          _this.tableLoading = false;
          if (resp && resp.status == 200) {
            var data = resp.data;
            _this.$message({type: data.status, message: data.msg});
            _this.loadSalaryAccountModeCfg();
          }
        });
      },
      updateAccountMode() {
        var _this = this;
        this.putRequest("/salary/accountMode/updateSalaryAccountMode",
          { 'id' : this.salaryAccountMode.id,
            'modeName' : this.salaryAccountMode.modeName,
            'salaryItems' : JSON.stringify(this.salaryAccountMode.salaryItems)
          }
        ).then(resp=> {
          _this.tableLoading = false;
          if (resp && resp.status == 200) {
            var data = resp.data;
            _this.$message({type: data.status, message: data.msg});
            _this.dialogVisible = false;
            _this.index = 0;
            _this.loadSalaryAccountModeCfg();
            _this.salaryAccountMode.items = {};
          }
        });
      },
      next(){
        var _this = this;
        if (this.index == this.salaryItem.length) {
          if (this.salaryAccountMode.id) {//更新
            _this.tableLoading = true;
            //先检查该账套是否有多个人在使用
            debugger
            this.putRequest("/salary/accountMode/countAccountModeRelation",{'accModeId' : this.salaryAccountMode.id})
              .then(resp=> {
                _this.tableLoading = false;
                if (resp && resp.status == 200) {
                  var data = resp.data;
                  if (data.count > 1) {
                    this.$confirm('有' + data.count +'个员工关联了此账套, 更改该账套的数据将影响这些员工，是否继续?', '提示', {
                      confirmButtonText: '确定',
                      cancelButtonText: '取消',
                      type: 'warning'
                    }).then(() => {
                        this.updateAccountMode();
                      }
                    ).catch(() => {
                      debugger
                      this.$message({
                        type: 'info',
                        message: '已取消账套更新'
                      });
                      _this.dialogVisible = false;
                      _this.index = 0;
                      _this.loadSalaryAccountModeCfg();
                      this.salaryAccountMode.items = {};
                    });
                  } else {
                    this.updateAccountMode();
                  }
                }
              });
          } else {//添加
            this.$prompt('请输入账套名称', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消'
            }).then(({value}) => {
              if (!value) {
                this.$message({type: 'error', message: '工资账套名称不能为空'});
                return;
              }

              this.postRequest("/salary/accountMode/addSalaryAccountMode",
                    { 'modeName' : value,
                      'salaryItems' : JSON.stringify(this.salaryAccountMode.salaryItems)
                    }
                  ).then(resp=> {
                if (resp && resp.status == 200) {
                  var data = resp.data;
                  _this.$message({type: data.status, message: data.msg});
                  _this.dialogVisible = false;
                  _this.index = 0;
                  _this.loadSalaryAccountModeCfg();
                  this.salaryAccountMode.items = {};
                }
              });
            }).catch(() => {
            });
          }
        } else {
          var key = _this.salaryItem[this.index];
          var value = _this.salaryAccountMode.items[key.id];
          if (!value || value < 0 ) {
            this.$message({type: 'error', message: '【' + key.itemName + '】不能为空和负数'});
            return;
          }

          if (value >= 10000 ) {
            this.$message({type: 'error', message: '【' + key.itemName + '】不能大于10000'});
            return;
          }

          var objValue = {};
          objValue['id'] = key.id;
          objValue['amt'] = value;
          objValue['itemName'] = key.itemName;
          objValue['itemType'] = key.itemType;
          objValue['isFixItem'] = key.isFixValueItem;

          _this.salaryAccountMode.salaryItems = _this.salaryAccountMode.salaryItems || {};
//          _this.salaryAccountMode.salaryItems['item_id_' + key.id] = objValue;
          _this.salaryAccountMode.salaryItems[key.id] = objValue;

          this.index++;
        }
      },
      loadSalaryAccountModeCfg(){
        this.tableLoading = true;
        var _this = this;
        this.getRequest("/salary/accountMode/findAllAccountMode").then(resp=> {
          _this.tableLoading = false;
          if (resp && resp.status == 200) {
              debugger
            _this.salaryAccountModeConfig = resp.data;
          }
        })
      },
      emptySalary(){
        this.salaryAccountMode.items = {};
        this.salaryAccountMode.id = '';
        this.index = 0;
      },

      saveSalaryItem() {
        var _this = this;
        debugger
        if(!_this.addSalaryItem.itemName || _this.addSalaryItem.itemName.trim().length == 0) {
          this.$message({type: 'error', message: '工资项目的名称不能为空'});
          return;
        }
        this.postRequest("/salary/item/saveSalaryItem",
          { 'itemName' : _this.addSalaryItem.itemName,
            'itemType' : _this.addSalaryItem.itemType === '收入' ? 1 : 2,
            'isFixValueItem' : _this.addSalaryItem.isFixedValue === '是' ? 1 : 0
          }
        ).then(resp=> {
          if (resp && resp.status == 200) {
            var data = resp.data;
            _this.$message({type: data.status, message: data.msg});
            _this.salaryItemDialogVisible = false;
            _this.addSalaryItem.itemName = '';
          }
        }).catch(() => {

        });
      },
      cancleAddSalaryItem(){
        var _this = this;
        _this.salaryItemDialogVisible = false;
      },

    },
    mounted: function () {
      this.loadSalaryItems();
      this.loadSalaryAccountModeCfg();
    }
  };
</script>
<style>
  .el-dialog__body {
    padding-bottom: 10px;
  }
</style>
