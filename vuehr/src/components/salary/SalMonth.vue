<template>
  <div>
    <el-container>
      <el-header style="text-align: left;padding-left: 0px;margin-top: 10px; margin-bottom: 0px;">
        <el-tag size="medium">请选择月份:</el-tag>
        <el-date-picker
          v-model="querySalaryMonth"
          unlink-panels
          size="mini"
          type="month"
          value-format="yyyy-MM"
          :editable=false
        />
        <el-button  @click="loadSalaryConfig" type="primary" size="mini" style="margin-left: 15px" icon="el-icon-search">搜 索</el-button>
        <el-button  @click="resetQuery" type="primary" size="mini" style="margin-left: 15px" icon="el-icon-search">重 置</el-button>
      </el-header>

      <el-main style="margin-left: 0px; margin-top: 0px; margin-right: 0px; padding-left: 0px;padding-top: 0px;">
        <div style="margin-top: 0px">
          <el-table
            :data="salaryMonthConfigs"
            size="mini"
            border
            stripe
            v-loading="tableLoading"
            style="width: 100%">
            <el-table-column
              align="center"
              label="时间"
              prop="salaryDate">
            </el-table-column>
            <el-table-column
              align="center"
              label="当前状态">
              <template slot-scope="scope">
                {{convertStatus(scope.row.status)}}
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              label="操作人">
              <template slot-scope="scope">
                {{scope.row.creatorName}}
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              label="操作时间">
              <template slot-scope="scope">
                {{scope.row.utime | formatDate}}
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              label="操作">
              <el-table-column label="初始化工资明细" align="center">
                <template slot-scope="scope">
                  <el-button
                    :disabled="scope.row.status == 0 ? false : true"
                    size="mini"
                    @click="handleInit(scope.$index, scope.row)">初始化
                  </el-button>
                </template>
              </el-table-column>
              <el-table-column label="导入工资明细" align="center">
                <template slot-scope="scope">
                  <el-upload
                    :show-file-list="false"
                    accept="application/vnd.ms-excel"
                    :action="'/salary/detail/importSalaryDetails?salaryMonth='+  scope.row.salaryDate"
                    :on-success="fileUploadSuccess"
                    :on-error="fileUploadError" :disabled="scope.row.status == 1  ? false : true "
                    :before-upload="beforeFileUpload" style="display: inline">
                    <el-button size="mini"  :disabled="scope.row.status == 1  ? false : true " type="success" :loading="fileUploadBtnText=='正在导入'">
                      {{fileUploadBtnText}}
                    </el-button>
                  </el-upload>

                </template>
              </el-table-column>
              <el-table-column label="计算个税" align="center">
                <template slot-scope="scope">
                  <el-button
                    :disabled="scope.row.status == 2 ? false : true"
                    size="mini"
                    @click="handleCalculatePersonTax(scope.$index, scope.row)">计算个税
                  </el-button>
                </template>
              </el-table-column>
              <el-table-column label="月末处理完成" align="center">
                <template slot-scope="scope">
                  <el-button
                    :disabled="scope.row.status == 3 ? false : true"
                    size="mini"
                    @click="handleCloseCloseComputeSalary(scope.$index, scope.row)">关账
                  </el-button>
                </template>
              </el-table-column>
              <!--<el-table-column label="删除" align="center">-->
                <!--<template slot-scope="scope">-->
                  <!--<el-button-->
                    <!--size="mini"-->
                    <!--type="danger"-->
                    <!--@click="handleDelete(scope.$index, scope.row)">删除-->
                  <!--</el-button>-->
                <!--</template>-->
              <!--</el-table-column>-->
            </el-table-column>
          </el-table>
          <div style="text-align: right;margin-top: 10px">
            <el-pagination
              background
              @current-change="currentChange"
              layout="prev, pager, next"
              :page-size="pageSize"
              :total="totalCount">
            </el-pagination>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>


<script>
  import ElInput from "../../../node_modules/element-ui/packages/input/src/input";
  import ElRow from "element-ui/packages/row/src/row";
  export default{
    components: {
      ElRow,
      ElInput
    },
    data(){
      return {
        tableLoading : false,
        querySalaryMonth : '',
        fileUploadBtnText : '导入工资明细',
        currentPage : 1,
        totalCount : -1,
        pageSize : 20,
        salaryMonthConfigs : [], //每月工资配置列表
      }
    },
    methods: {
      resetQuery() {
        this.querySalaryMonth = '';
      },
      currentChange(currentPage){
        this.currentPage = currentPage;
      },

      //加载工资配置
      loadSalaryConfig(){
        var _this = this;
        var queryStr = "&salaryMonth=" + this.querySalaryMonth
          + "&pageIndex=" + this.currentPage + "&pageSize=20"
        ;
        this.getRequest("/salary/compute/querySalaryMonthConfig?" + queryStr).then(resp=> {
          _this.tableLoading = false;
          if (resp && resp.status == 200) {
            var data = resp.data;
            _this.salaryMonthConfigs = data.salaryMonthConfigs;
            _this.totalCount = data.count;
          }
        });
      },

      //导入成功
      fileUploadSuccess(response, file, fileList){
        var _this = this;
        if (response) {
          this.$message({type: response.status, message: response.msg});
        }
        this.fileUploadBtnText = '导入工资明细';

        _this.loadSalaryConfig();

      },
      fileUploadError(err, file, fileList){
        this.$message({type: 'error', message: "导入失败!"});
        this.fileUploadBtnText = '导入工资明细';
      },
      beforeFileUpload(file){
        this.fileUploadBtnText = '正在导入';
      },

      //状态转换
      convertStatus(a){
          if (a === 0) {
              return "请初始化工资明细";
          } else if (a === 1) {
              return "请导入工资明细"
          } else if (a === 2) {
              return "请计算个税";
          } else if (a === 3) {
            return "已完成个税计算"
          } else if (a === 9) {
              return "已完成当月工资计算"
          }
          return "未知的状态";
      },

      //初始化
      handleInit(index, data) {
        var _this = this;
        _this.tableLoading = true;
        this.getRequest("/salary/compute/initSalaryDetail?salaryDate=" + data.salaryDate).then(resp=> {
          if (resp && resp.status == 200) {
            var data = resp.data;
            _this.$message({type: data.status, message: data.msg});
          }
          _this.loadSalaryConfig("");
          _this.tableLoading = false;
        });
      },

      //计算个税
      handleCalculatePersonTax(index, data){
        var _this = this;
        _this.tableLoading = true;
        this.getRequest("/salary/compute/calculatePersonTax?salaryDate=" + data.salaryDate).then(resp=> {
          if (resp && resp.status == 200) {
            var data = resp.data;
            _this.$message({type: data.status, message: data.msg});
          }
        });
        _this.loadSalaryConfig("");
        _this.tableLoading = false;
      },

      //关账
      handleCloseCloseComputeSalary(index, data) {
        var _this = this;
        _this.tableLoading = true;
        this.getRequest("/salary/compute/closeComputeSalary?salaryDate=" + data.salaryDate).then(resp=> {
          if (resp && resp.status == 200) {
            var data = resp.data;
            _this.$message({type: data.status, message: data.msg});
          }
        });
        _this.loadSalaryConfig("");
        _this.tableLoading = false;
      }

    },

    mounted:function () {
      Date.prototype.format = function(fmt) {
        var o = {
          "M+" : this.getMonth()+1,                 //月份
          "d+" : this.getDate(),                    //日
          "h+" : this.getHours(),                   //小时
          "m+" : this.getMinutes(),                 //分
          "s+" : this.getSeconds(),                 //秒
          "q+" : Math.floor((this.getMonth()+3)/3), //季度
          "S"  : this.getMilliseconds()             //毫秒
        };
        if(/(y+)/.test(fmt)) {
          fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
        }
        for(var k in o) {
          if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
          }
        }
        return fmt;
      }

      var nowDate = new Date();
      nowDate.setMonth(nowDate.getMonth() - 1);
//      this.querySalaryMonth = nowDate.format("yyyy-MM");
      this.tableLoading = false;
      this.loadSalaryConfig();
    }
  }

</script>
