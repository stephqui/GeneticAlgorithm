package generique.metier.selection;

import java.util.function.BiConsumer;

import generique.metier.entite.Individu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
public class Selecteur<T extends Individu<?>> {
	
	@Setter
	@NonNull
	private BiConsumer<T, Integer> selecteurIndividu;
	
	@Getter
	@Setter
	private Integer seuil;

	public void selectionner(T individu) {
		this.selecteurIndividu.accept(individu, this.seuil);
	}
	
}
