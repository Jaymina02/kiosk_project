package kr.co.sist.user.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.co.sist.user.dao.MenuDAO;
import kr.co.sist.user.dto.MenuOptionDTO;

public class MenuService {

	public List<String> searchMenuOptions(){
		List<String> listMenuOption = new ArrayList<String>();
		//분할된 컬럼의 값(DTO)을 CSV Data 변환하는 업무
		MenuDAO mDAO=MenuDAO.getInstance();
		
		try {
			List<MenuOptionDTO> tempList= mDAO.selectMenuOptions();
			StringBuilder csvData=new StringBuilder();
			
			for(MenuOptionDTO mDTO: tempList ) {
				csvData.delete(0, csvData.length());
				//분할된 값을 CSV데이터로 생성
				csvData
				.append(mDTO.getOptionId()).append(",")
				.append(mDTO.getOptionType()).append(",")
				.append(mDTO.getTemperature()).append(",")
				.append(mDTO.getSize()).append(",")
				.append(mDTO.getOptionName()).append(",")
				.append(mDTO.getMenuName()).append(",")
				.append(mDTO.getOptionPrice()).append(",")
				.append(mDTO.getBasePrice()).append(",")
				.append(mDTO.getQuantity()).append(",")
				.append(mDTO.getTotalAmount()).append(",")
				.append(mDTO.getThumbnailPath()).append(",")
				.append(mDTO.getExtraOptionNames()).append(",");
				
				//생성된 문자열(CSV)을 화면에 보여줄 리스트(dlmMember)에서
				//사용할 리스트(listMenuOption)에 할당한다.
				listMenuOption.add(csvData.toString());
				
			}//end for
			
		} catch (SQLException e) {
			e.printStackTrace();
		}//end catch
		
		return listMenuOption;
	}//searchMenuOptions
	
}//class
