package live.lingting.virtual.currency.tronscan;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.Account;
import live.lingting.virtual.currency.contract.Contract;
import live.lingting.virtual.currency.endpoints.Endpoints;
import live.lingting.virtual.currency.tronscan.Transaction.RawData;
import live.lingting.virtual.currency.tronscan.TriggerResult.TransferBroadcastResult;
import live.lingting.virtual.currency.tronscan.TriggerResult.Trc10TransferGenerateResult;
import live.lingting.virtual.currency.tronscan.TriggerResult.Trc20TransferGenerateResult;
import live.lingting.virtual.currency.tronscan.TriggerResult.TriggerConstantResult;
import live.lingting.virtual.currency.tronscan.TriggerResult.TrxTransferGenerateResult;
import live.lingting.virtual.currency.util.AbiUtil;
import live.lingting.virtual.currency.util.JsonUtil;
import live.lingting.virtual.currency.util.TronscanUtil;

/**
 * 使用请参考 <a href=
 * "https://cn.developers.tron.network/reference#%E8%A7%A6%E5%8F%91%E6%99%BA%E8%83%BD%E5%90%88%E7%BA%A6">文档</a>
 *
 * @author lingting 2020/12/25 20:56
 */
@Getter
@Setter
@Accessors(chain = true)
public class TriggerRequest<T extends TriggerResult> {

	/**
	 * 请求的url
	 */
	@JsonIgnore
	private String url;

	/**
	 * 返回值转换的类
	 */
	@JsonIgnore
	private Class<T> target;

	@JsonProperty("owner_address")
	private String ownerAddress;

	@JsonProperty("contract_address")
	private String contractAddress;

	@JsonProperty("function_selector")
	private String functionSelector;

	private String parameter;

	/**
	 * 最大消耗的 TRX 数量, 以 SUN 为单位
	 */
	@JsonProperty("fee_limit")
	private BigInteger feeLimit;

	@JsonProperty("call_value")
	private BigInteger callValue;

	/**
	 * trc10 合约地址
	 */
	@JsonProperty("asset_name")
	private String assetName;

	@JsonProperty("to_address")
	private String toAddress;

	/**
	 * 转账数量
	 */
	@JsonProperty("amount")
	private BigInteger amount;

	@JsonProperty("permission_id")
	private BigDecimal permissionId;

	/**
	 * 账户地址是否为 Base58check 格式, 为 false, 使用 HEX 地址
	 */
	private Boolean visible = true;

	/**
	 * 用于广播交易,
	 */
	@JsonProperty("txID")
	private String txId;

	/**
	 * 用于广播交易,
	 */
	@JsonProperty("raw_data")
	private RawData rawData;

	/**
	 * 用于广播交易
	 */
	@JsonProperty("raw_data_hex")
	private String rawDataHex;

	/**
	 * 用于广播交易
	 */
	@JsonProperty("signature")
	private List<String> signature = null;

	public static TriggerRequest<TriggerConstantResult> trc20Decimals(Endpoints endpoints, Contract contract) {
		return new TriggerRequest<TriggerConstantResult>()
				// 目标类型
				.setTarget(TriggerConstantResult.class)
				// url
				.setUrl(endpoints.getHttpUrl("wallet/triggerconstantcontract"))
				// owner_address
				.setOwnerAddress("T9yD14Nj9j7xAB4dbGeiX9h8unkKHxuWwb")
				// contract_address
				.setContractAddress(contract.getHash())
				// function_selector
				.setFunctionSelector("decimals()");
	}

	/**
	 * 查询指定地址, 指定trc20合约余额
	 * @param endpoints 节点
	 * @param contract 合约
	 * @param address 地址
	 * @return live.lingting.virtual.currency.tronscan.TriggerRequest<live.lingting.virtual.currency.tronscan.TriggerResult.TriggerConstantResult>
	 * @author lingting 2021-02-04 14:52
	 */
	public static TriggerRequest<TriggerConstantResult> trc20BalanceOf(Endpoints endpoints, Contract contract,
			String address) {
		return new TriggerRequest<TriggerConstantResult>()
				// 目标类型
				.setTarget(TriggerConstantResult.class)
				// url
				.setUrl(endpoints.getHttpUrl("wallet/triggerconstantcontract"))
				// owner_address
				.setOwnerAddress("T9yD14Nj9j7xAB4dbGeiX9h8unkKHxuWwb")
				// contract_address
				.setContractAddress(contract.getHash())
				// function_selector
				.setFunctionSelector("balanceOf(address)")
				// 参数, 查询目标
				.setParameter(TronscanUtil.encodeAddressParam(address));
	}

	/**
	 * 触发trc20合约转账函数, 生成转账数据
	 * @param from 转出地址
	 * @param to 收款人
	 * @param amount 转出数量
	 * @param contract 触发合约
	 * @param feeLimit 费用
	 * @param callValue 支付给合约的费用
	 * @author lingting 2020-12-25 20:50
	 */
	public static TriggerRequest<Trc20TransferGenerateResult> trc20TransferGenerate(Endpoints endpoints, Account from,
			String to, BigInteger amount, Contract contract, BigInteger feeLimit, BigInteger callValue) {

		return new TriggerRequest<Trc20TransferGenerateResult>()
				// 目标
				.setTarget(Trc20TransferGenerateResult.class)
				// url
				.setUrl(endpoints.getHttpUrl("wallet/triggersmartcontract"))
				// 合约地址
				.setContractAddress(contract.getHash())
				// 方法
				.setFunctionSelector("transfer(address,uint256)")
				// 费用, 以 sun 为单位 1trx = 10^6 sun, 最大值为 10^sun 即 1000TRX
				.setFeeLimit(feeLimit)
				// call_value
				.setCallValue(callValue)
				// 转账人
				.setOwnerAddress(from.getAddress())
				// 参数, 里面 有 转账金额 收款人
				.setParameter(
						// 收款人
						TronscanUtil.encodeAddressParam(to)
								// 转账金额
								+ AbiUtil.encodeUint256Params(amount));
	}

	/**
	 * 广播交易
	 * @param endpoints 节点
	 * @param txId txId
	 * @param rawData rawData
	 * @param rawDataHex rawDataHex
	 * @param signature 签名
	 * @return live.lingting.virtual.currency.tronscan.TriggerRequest
	 * @author lingting 2020-12-25 22:57
	 */
	public static TriggerRequest<TransferBroadcastResult> trc10TransferBroadcast(Endpoints endpoints, String txId,
			RawData rawData, String rawDataHex, String signature) {
		return new TriggerRequest<TransferBroadcastResult>()
				// 目标
				.setTarget(TransferBroadcastResult.class)
				// url
				.setUrl(endpoints.getHttpUrl("wallet/broadcasttransaction"))
				// tx id
				.setTxId(txId)
				// raw data
				.setRawData(rawData)
				// raw data hex
				.setRawDataHex(rawDataHex)
				// 签名
				.addSignature(signature);
	}

	/**
	 * 触发trc10合约转账函数, 生成转账数据
	 * @param from 转出地址
	 * @param to 收款人
	 * @param amount 转出数量
	 * @param contract 触发合约
	 * @author lingting 2020-12-25 20:50
	 */
	public static TriggerRequest<Trc10TransferGenerateResult> trc10TransferGenerate(Endpoints endpoints, Account from,
			String to, BigInteger amount, Contract contract) {

		return new TriggerRequest<Trc10TransferGenerateResult>()
				// 目标
				.setTarget(Trc10TransferGenerateResult.class)
				// url
				.setUrl(endpoints.getHttpUrl("wallet/transferasset"))
				// 合约地址
				.setAssetName(contract.getHash())
				// 转账人
				.setOwnerAddress(from.getAddress())
				// 收款人
				.setToAddress(to)
				// 转账数量
				.setAmount(amount);
	}

	/**
	 * 触发trx合约转账函数, 生成转账数据
	 * @param from 转出地址
	 * @param to 收款人
	 * @param amount 转出数量
	 * @param contract 触发合约
	 * @author lingting 2020-12-25 20:50
	 */
	public static TriggerRequest<TrxTransferGenerateResult> trxTransferGenerate(Endpoints endpoints, Account from,
			String to, BigInteger amount, Contract contract) {

		return new TriggerRequest<TrxTransferGenerateResult>()
				// 目标
				.setTarget(TrxTransferGenerateResult.class)
				// url
				.setUrl(endpoints.getHttpUrl("wallet/createtransaction"))
				// 转账人
				.setOwnerAddress(from.getAddress())
				// 收款人
				.setToAddress(to)
				// 转账数量
				.setAmount(amount);
	}

	public TriggerRequest<T> addSignature(String s) {
		if (signature == null) {
			signature = new ArrayList<>();
		}
		signature.add(s);
		return this;
	}

	/**
	 * 执行请求
	 * @author lingting 2020-12-25 21:04
	 */
	public T exec() throws JsonProcessingException {
		// 执行请求
		HttpRequest request = HttpRequest.post(url).body(JsonUtil.toJson(this));
		String response = request.execute().body();

		T obj = JsonUtil.toObj(response, target);
		obj.setResponse(response);
		return obj;
	}

}
