<template>
  <el-container>
    <el-header style="padding: 0px;display:flex;justify-content:space-between;align-items: center">
      <div style="display: inline">
        <el-select size="mini" v-model="queryType" placeholder="请选择查询条件">
          <el-option
            label="姓名"
            value="name">
          </el-option>
          <el-option
            label="工号"
            value="workId">
          </el-option>
          <el-option
            label="手机号"
            value="telno">
          </el-option>
        </el-select>
        <el-input
          placeholder="请输入查询条件"
          clearable
          style="width: 200px;margin: 0px;padding: 0px;"
          size="mini"
          prefix-icon="el-icon-search"
          v-model="queryValue"
          >
        </el-input>

        <label>部门：
            <el-popover
              v-model="showOrHidePop"
              placement="right"
              title="请选择部门"
              trigger="manual">
              <el-tree :data="deps" :default-expand-all="true" :props="defaultProps" :expand-on-click-node="false"
                       @node-click="handleNodeClick"></el-tree>
              <div slot="reference"
                   style="width: 150px;height: 26px;display: inline-flex;font-size:13px;border: 1px;border-radius: 5px;border-style: solid;padding-left: 13px;box-sizing:border-box;border-color: #dcdfe6;cursor: pointer;align-items: center"
                   @click.left="showDepTree" v-bind:style="{color: depTextColor}">
                {{departmentName}}
              </div>
            </el-popover>
        </label>
         账套：
        <el-select size="mini" v-model="queryAccModeId" placeholder="请选择账套">
          <el-option
            v-for="item in salaryAccountModes"
            :key="item.id"
            :label="item.modeName"
            :value="item.id">
          </el-option>
        </el-select>

        <el-button @click="loadEmps" type="primary" size="mini" style="margin-left: 5px" icon="el-icon-search" >搜索</el-button>
      </div>

    </el-header>


    <el-main style="padding-left: 0px;padding-top: 0px">
      <div style="margin-top: 0px">
        <el-table
          :data="emps"
          size="mini"
          border
          stripe
          v-loading="tableLoading"
          style="width: 100%">
          <el-table-column
            type="selection"
            align="left"
            width="55">
          </el-table-column>
          <el-table-column
            prop="name"
            align="left"
            fixed
            label="姓名"
            width="120">
          </el-table-column>
          <el-table-column
            prop="department.name"
            align="center"
            label="所属部门">
          </el-table-column>
          <el-table-column
            prop="positionName"
            width="120"
            align="center"
            label="岗位">
          </el-table-column>
          <el-table-column
            prop="phone"
            width="120"
            label="电话号码">
          </el-table-column>
          <el-table-column
            prop="workid"
            width="120"
            align="left"
            label="工号">
          </el-table-column>
          <el-table-column label="工资账套" align="center">
            <template slot-scope="scope">
              <el-tooltip placement="right">
                <div slot="content">
                  <template v-if="scope.row.salaryAccountModeMap">

                    <div v-for="item in scope.row.salaryAccountModeMap">
                      <el-tag size="mini">{{item.itemName}}</el-tag>
                      ￥{{item.amt}}
                    </div>
                  </template>
                </div>
                <el-tag size="mini">{{scope.row.salaryAccountMode ? scope.row.salaryAccountMode.modeName : '暂未设置'}}</el-tag>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <el-popover
                placement="right"
                width="200"
                @hide="updateSalaryCfg(scope.row.id)"
                :key="scope.row.id"
                trigger="click">
                <el-select size="mini" v-model="sid" placeholder="请选择账套">
                  <el-option
                    v-for="item in salaryAccountModes"
                    :key="item.id"
                    :label="item.modeName"
                    :value="item.id">
                  </el-option>
                </el-select>
                <el-button
                  size="mini"
                  slot="reference"
                  type="danger" @click="showUpdateView(scope.row)">修改账套
                </el-button>
              </el-popover>
            </template>
          </el-table-column>
        </el-table>
        <div style="text-align: right;margin-top: 10px">
          <el-pagination
            background
            @current-change="currentChange"
            layout="prev, pager, next"
            :total="totalCount">
          </el-pagination>
        </div>
      </div>
    </el-main>
  </el-container>
</template>
<script>
  export default{
    data(){
      return {
        emps: [],
        deps: [],
        salaryAccountModes: [],
        tableLoading: false,
        totalCount: -1,
        sid: '',
        osid: '',
        queryAccModeId: '',   //用于查询的账套id
        queryType: '',  //查询条件的类型
        queryValue: '', //查询条件的内容
        currentPage: 1,
        showOrHidePop: false,
        showOrHidePop2: false,
        depTextColor: '#c0c4cc',
        departmentId : '',
        departmentName: '请选择部门',
        defaultProps: {
          label: 'name',
          isLeaf: 'leaf',
          children: 'children'
        },
        emp: {
          departmentId: '',
          departmentName: '所属部门...',
        },
      }
    },
    mounted: function () {
      this.loadEmps();
      this.loadDeps(-1);
      this.loadAccountModes();
    },
    methods: {
      showUpdateView(data){
        this.loadAccountModes();
        if (data.salary) {
          this.sid = data.salary.id;
          this.osid = data.salary.id;
        } else {
          this.sid = '';
          this.osid = '';
        }
      },
      loadAccountModes(){
        var _this = this;
        if (!_this.salaryAccountModes){
            return;
        }
        this.getRequest("/salary/accountMode/findAllAccountMode").then(resp=> {
          if (resp && resp.status == 200) {
            _this.salaryAccountModes = resp.data;
          }
        })
      },
      updateSalaryCfg(eid){
        var _this = this;
        if(this.osid==this.sid) {
          return;
        }
        this.putRequest("/empAccModeRelation/updateRelation/", {eid: eid, acmId: this.sid}).then(resp=> {
          if(resp&&resp.status==200) {
            var data = resp.data;
            _this.$message({type: data.status, message: data.msg});
            _this.loadEmps();
          }
        })
      },
      handleNodeClick(data) {
        this.departmentName = data.name;
        this.departmentId = data.id;
        this.showOrHidePop = false;
        this.depTextColor = '#606266';
      },
      handleNodeClick2(data) {
        this.emp.departmentName = data.name;
        this.emp.departmentId = data.id;
        this.showOrHidePop2 = false;
        this.depTextColor = '#606266';
      },
      showDepTree(){
        this.showOrHidePop = !this.showOrHidePop;
      },
      showDepTree2(){
        this.showOrHidePop2 = !this.showOrHidePop2;
      },
      currentChange(currentPage){
        this.currentPage = currentPage;
        this.loadEmps();
      },
      loadEmps(){
        this.tableLoading = true;
        var _this = this;
        var queryStr = "queryType=" + this.queryType + "&queryVaule="
          + this.queryValue + "&departmentId=" + this.departmentId + "&queryAccModeId=" + this.queryAccModeId
          + "&page=" + this.currentPage + "&size=10"
        ;
        this.getRequest("/empAccModeRelation/findEmpAccModeRelationByPage?" + queryStr).then(resp=> {
          _this.tableLoading = false;
          if (resp && resp.status == 200) {
            var data = resp.data;
            _this.emps = data.emps;
            _this.totalCount = data.count;
          }
        });
      },
      loadDeps(pid) {
        var _this = this;
        this.getRequest("/dep/findDepartments?pid=" + pid).then(resp=> {
          if (resp && resp.status == 200) {
            _this.deps = resp.data;
          }
        });
      }
    }
  }
</script>
