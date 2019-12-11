package upa.gui;

import upa.db.entity.Image;
import upa.gui.model.ImageTableModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class SimilarImages extends JFrame
{
    private final JPanel imageDisplay;
    private JPanel contentPane;
    private JTable imagesTable;
    private JPanel imageDisplayPane;

    private BufferedImage imageToBeDisplayedFiltered;
    private int selectedImageIndex;
    private Image selectedImage;
    private BufferedImage imageToBeDisplayed;

    public SimilarImages(Image image)
    {
        //-----------------------------------------------------dd--
        //  Window setup
        //-----------------------------------------------------dd--

        setTitle("Similarity-ordered image table");
        setContentPane(contentPane);
        setSize(800, 600);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });


        //-----------------------------------------------------dd--
        //  Data setup
        //-----------------------------------------------------dd--

        ImageTableModel imageTableModel = new ImageTableModel();
        imageTableModel.SetEditable(false);
        imagesTable.setModel(imageTableModel);
        imagesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imagesTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;

            final DefaultListSelectionModel target = (DefaultListSelectionModel) e.getSource();
            // save current selection
            SaveImageSelection(target.getAnchorSelectionIndex());
        });
        imageTableModel.SetImagesList(image.GetAllSimilar());
        imagesTable.doLayout();

        this.imageDisplay = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                if (imageToBeDisplayed == null)
                    return;

                Dimension parentSize = getParent().getSize();
                BufferedImage imageData = imageToBeDisplayed;
                float widthResizeRatio = (float) parentSize.getWidth() / (float) imageData.getWidth();
                float heightResizeRatio = (float) parentSize.getHeight() / (float) imageData.getHeight();
                float resizeRatio = Math.min(Math.min(heightResizeRatio, widthResizeRatio), 1);

                int width = (int) (imageData.getWidth() * resizeRatio);
                int height = (int) (imageData.getHeight() * resizeRatio);
                BufferedImage imageAfter = new BufferedImage(width, height, imageData.getType());

                AffineTransform transform = new AffineTransform();
                transform.scale(resizeRatio, resizeRatio);

                AffineTransformOp transformOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
                transformOp.filter(imageData, imageAfter);
                imageToBeDisplayedFiltered = imageAfter;

                g.drawImage(imageToBeDisplayedFiltered, 0, 0, null);
                setPreferredSize(new Dimension(imageToBeDisplayedFiltered.getWidth(), imageToBeDisplayedFiltered.getHeight()));
            }
        };
        imageDisplayPane.add(imageDisplay);


        //-----------------------------------------------------dd--
        //  Listeners setup
        //-----------------------------------------------------dd--

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void SaveImageSelection(int index)
    {
        selectedImageIndex = index;
        selectedImage = GetImageTableModel().Get(index);

        SetImageToBeDisplayed(selectedImage);
    }

    private void SetImageToBeDisplayed(Image image)
    {
        try
        {
            imageToBeDisplayed = ImageIO.read(image.image.getDataInStream());
            try
            {
                imageToBeDisplayed = ImageIO.read(image.image.getDataInStream());
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                screenSize.width = screenSize.width - 200;
                screenSize.height = screenSize.height - 200;
                Dimension imageSize = new Dimension(imageToBeDisplayed.getWidth(), imageToBeDisplayed.getHeight());

                imageSize.width = Math.min(screenSize.width, imageSize.width);
                imageSize.height = Math.min(screenSize.height, imageSize.height);

                float widthResizeRatio = (float) imageSize.width / (float) imageToBeDisplayed.getWidth();
                float heightResizeRatio = (float) imageSize.height / (float) imageToBeDisplayed.getHeight();
                if (widthResizeRatio < heightResizeRatio)
                {
                    imageSize.width = (int) (imageSize.width * widthResizeRatio);
                    imageSize.height = (int) (imageSize.height * heightResizeRatio);
                }
                else
                {
                    imageSize.width = (int) (imageSize.width * heightResizeRatio);
                    imageSize.height = (int) (imageSize.height * widthResizeRatio);
                }

                imageDisplay.setPreferredSize(imageSize);
                repaint();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            setSize(getWidth() + 1, getHeight() + 1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private ImageTableModel GetImageTableModel()
    {
        return (ImageTableModel) imagesTable.getModel();
    }


    //=====================================================================dd==
    // OTHER ACTION METHODS
    //=====================================================================dd==

    private void onCancel()
    {
        dispose();
    }
}
