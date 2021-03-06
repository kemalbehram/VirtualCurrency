package live.lingting.virtual.currency.etherscan;

import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import live.lingting.virtual.currency.Transaction;
import live.lingting.virtual.currency.endpoints.InfuraEndpoints;
import live.lingting.virtual.currency.properties.InfuraProperties;
import live.lingting.virtual.currency.service.PlatformService;
import live.lingting.virtual.currency.service.impl.InfuraServiceImpl;

/**
 * @author lingting 2021/1/10 15:54
 */
@Slf4j
public class Select {

	private static final PlatformService service;

	static {
		service = new InfuraServiceImpl(new InfuraProperties()
				// 节点
				.setEndpoints(InfuraEndpoints.MAINNET)
				// project Id
				.setProjectId("9d0efcd0739f4d2f8dae3da3a49051f0"));
	}

	@Test
	@SneakyThrows
	public void etc() {
		Optional<Transaction> optional = service
				.getTransactionByHash("0x17dfd34a127eab2be3c17e9567d42ab39fde92907b23bb190071744eff2be9e0");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getValue());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getTime());
		}
		optional = service.getTransactionByHash("0x17dfd34a3e7eab2be3c17e9567d42ab39fde92907b23bb190071744eff2be9e0");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getValue());
			System.out.println(transaction.getContract());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getTime());
		}
	}

	@Test
	@SneakyThrows
	public void erc20() {
		Optional<Transaction> optional = service
				.getTransactionByHash("0x17dfd34a127eab2be3c17e9567d42ab39fde92907b23bb190071744eff2be9e0");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getValue());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getTime());
		}
		optional = service.getTransactionByHash("0xf07f075c1fad8e87bdaa095c07ae3ccab5a0a1f0d8c0460769326d10d5a07e4b");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getValue());
			System.out.println(transaction.getContract());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getTime());
		}
	}

	@Test
	@SneakyThrows
	public void valid() {
		System.out.println(service.validate("2131231231"));
		System.out.println(service.validate("0x41c559f4f785664371ffe3ff489029126488849c"));
		System.out.println(service.validate("0x5fa7e29ffcd685f9fd31d7fe6940be5ef7cb6358"));
	}

}
