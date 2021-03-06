package live.lingting.virtual.currency.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.enums.ApiPlatform;

/**
 * 虚拟货币处理配置
 *
 * @author lingting 2020-09-01 17:19
 */
@Data
@Accessors(chain = true)
public class VirtualCurrencyProperties {

	/**
	 * 平台，使用哪个平台
	 * <p>
	 * 请确定获取处理类时传入的是对应平台的参数，具体请查阅 {@link ApiPlatform} 类
	 */
	private ApiPlatform apiPlatform;

}
