package com.jzo2o.orders.base.handler;

import com.jzo2o.orders.base.enums.OrderPayStatusEnum;
import com.jzo2o.orders.base.enums.OrderStatusEnum;
import com.jzo2o.orders.base.model.dto.OrderSnapshotDTO;
import com.jzo2o.orders.base.model.dto.OrderUpdateStatusDTO;
import com.jzo2o.orders.base.service.IOrdersCommonService;
import com.jzo2o.statemachine.core.StatusChangeEvent;
import com.jzo2o.statemachine.core.StatusChangeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("order_payed")
public class OrderPayedHandler implements StatusChangeHandler<OrderSnapshotDTO> {
    @Resource
    private IOrdersCommonService ordersService;

    /**
     * 订单支付处理逻辑
     *
     * @param bizId   业务id
     * @param bizSnapshot 快照
     */
    @Override
    public void handler(String bizId, StatusChangeEvent statusChangeEventEnum, OrderSnapshotDTO bizSnapshot) {
        OrderUpdateStatusDTO orderUpdateStatusReqDTO=new OrderUpdateStatusDTO();
        orderUpdateStatusReqDTO.setId(bizSnapshot.getId());//订单id
        orderUpdateStatusReqDTO.setOriginStatus(OrderStatusEnum.NO_PAY.getStatus());//原状态
        orderUpdateStatusReqDTO.setTargetStatus(OrderStatusEnum.DISPATCHING.getStatus());//目标状态
        orderUpdateStatusReqDTO.setPayStatus(OrderPayStatusEnum.PAY_SUCCESS.getStatus());//支付状态
        orderUpdateStatusReqDTO.setTradingOrderNo(bizSnapshot.getTradingOrderNo());//交易订单号
        orderUpdateStatusReqDTO.setTransactionId(bizSnapshot.getThirdOrderId());//交易流水号
        orderUpdateStatusReqDTO.setPayTime(bizSnapshot.getPayTime());//支付时间
        orderUpdateStatusReqDTO.setTradingChannel(bizSnapshot.getTradingChannel());//交易渠道

        Integer result = ordersService.updateStatus(orderUpdateStatusReqDTO);
        if(result<=0){
            throw new RuntimeException("订单"+bizSnapshot.getId()+"更新订单状态失败");
        }
    }
}