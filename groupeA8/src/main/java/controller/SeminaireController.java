package controller;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import com.sun.jdo.api.persistence.support.Transaction;

import entities.Etudiant;
import entities.Seminaire;
import sessionejb.GestionSeminaireEJB;

@Named
@SessionScoped
public class SeminaireController implements Serializable{
    
    /**
	 * 
	 */
	private boolean visibleAdd = true;
	private boolean visibleDelete = false;
	private static final long serialVersionUID = 1L;
	@EJB
    private GestionSeminaireEJB gestionSeminaire;
    private List<Seminaire> seminaires = new ArrayList<Seminaire>();
    private Seminaire seminaire;
    private String section;    
    
    public SeminaireController() {}
    
    public void init() {
        seminaire = new Seminaire();
    }
    
    public boolean isVisibleAdd() {
    	return visibleAdd;
    }
    
    public boolean isVisibleDelete() {
    	return visibleDelete;
    }

    public void verifEtudiantInscrit(Etudiant etu) {
    	int res = gestionSeminaire.etudiantInscrit(seminaire.getId(),etu.getId());
    	if(res == 0) {
    		visibleAdd = true;
    		visibleDelete = false;
    	}else {
    		visibleDelete = true;
    		visibleAdd = false;
    	}
    }
    
    public Seminaire getSeminaire() {
		if(seminaire == null) {
			init();
		}
    	return seminaire;
	}

	public void setSeminaire(Seminaire seminaire) {
		this.seminaire = seminaire;
	}

	public List<Seminaire> getSeminaires(){
        seminaires = gestionSeminaire.selectAll();
        return seminaires;
    }
    
	public List<Seminaire> getSeminairesSection(String idSection){
        seminaires = gestionSeminaire.selectSeminaireSection(idSection);
        return seminaires;
    }
	
    public void deleteSeminaire(Seminaire seminaire) {
        gestionSeminaire.deleteSemi(seminaire);;
        seminaires = gestionSeminaire.selectAll();
    }
    
    public String addSeminaire() throws ParseException {
		FacesContext context = FacesContext.getCurrentInstance();
		
		/*DateFormat dateFormat = new SimpleDateFormat(
	            "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		dateFormat.parse(seminaire.getDate());
		
		String date = dateFormat.format("DD-MM-YYYY");
		String dateFinal = date.format(date);*/
		
		//seminaire.setDate(dateFinal);
		
		SimpleDateFormat formatnow = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.FRENCH);
		SimpleDateFormat formatneeded=new SimpleDateFormat("YYYY-MM-dd");
		Date date1 = formatnow.parse(seminaire.getDate());
		String date2 = formatneeded.format(date1);
		
		seminaire.setDate(date2);
		
    	seminaire.setSection(section);
        gestionSeminaire.addSemi(seminaire);
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "S�minaire cr�� !", null));
		context.getExternalContext().getFlash().setKeepMessages(true);
        return "ListeSeminaire.xhtml?face-redirect=true";
    }

    public String ajouterEtudiant(Etudiant e) {
    	Etudiant tmpEtudiant = e;
    	tmpEtudiant.setId(e.getId());
    	seminaire.ajouterEtudiant(tmpEtudiant);
    	gestionSeminaire.updateSeminaire(seminaire);
    	
    	FacesContext context = FacesContext.getCurrentInstance();
    	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Etudiant ajout� au s�minaire : " + seminaire.getNomSeminaire(), null));
    	return "AjouterEtudiantSeminaire.xhtml?face-redirect=true";
    	
    }
    
	public String getSection() {
		return section;
	}

	public String setSection(String section) {
		this.section = section;
		return "ListeSeminaire.xhtml?face-redirect=true";
	}
	
	
	public String onPageLoad() { 
	    // Do your stuff here.
		return "ListeSeminaire.xhtml?face-redirect=true";
	}
	
	public String navAjouterEtudiantSerminaire(Seminaire s) {
		seminaire = s;
		return "AjouterEtudiantSeminaire.xhtml?face-redirect=true";
	}
	
	public List<Seminaire> getSeminairesId(int idEtu){
		seminaires = gestionSeminaire.selectSeminaireId(idEtu);
		return seminaires;
	}
	
	public long getCountId(int idEtu){
		long count = gestionSeminaire.getSeminaireId(idEtu);
		return count;
	}
	
	public long getCountSeminaire(String section){
		long count = gestionSeminaire.getCountSeminaireSection(section);
		return count;
	}
	
	public void clearNom() {
		init();
		seminaire.setNomSeminaire("");
	}

}